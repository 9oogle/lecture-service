-- =====================================================================
-- lecture-service 초기 스키마 (PostgreSQL 16+)
--
-- 적용 대상 :
--   1) 로컬 docker-compose 의 postgres 컨테이너 최초 부팅 시
--      (volumes: ./init:/docker-entrypoint-initdb.d 로 마운트)
--   2) dev/운영 환경 DB 최초 1회 수동 실행
--
-- 주의 :
--   - JPA 는 application.yaml 에서 default_schema=lecture 로 설정됨
--   - 따라서 모든 테이블은 lecture 스키마 안에 생성한다
--   - ddl-auto: validate 가 통과하도록 컬럼명/타입은 엔티티와 1:1 일치
--
-- 변경 이력 :
--   v1 (2026-05-03) 최초 작성 - p_lecture, p_chapter, p_enrollment
-- =====================================================================

-- 0) 확장 모듈 (UUID 사용)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1) 스키마
CREATE SCHEMA IF NOT EXISTS lecture;
SET search_path TO lecture, public;


-- =====================================================================
-- p_lecture : 강의
-- =====================================================================
CREATE TABLE IF NOT EXISTS lecture.p_lecture
(
    id               UUID PRIMARY KEY,

    -- @Embedded Instructor
    instructor_id    UUID         NOT NULL,
    instructor_name  VARCHAR(100) NOT NULL,

    -- 카테고리
    category         VARCHAR(50)  NOT NULL,

    -- @Embedded LectureContent
    title            VARCHAR(200) NOT NULL,
    subtitle         VARCHAR(300),
    description      TEXT,

    -- 수강기간 정책
    duration_policy  VARCHAR(20)  NOT NULL,

    -- @Embedded Money (price)
    price            BIGINT       NOT NULL,

    -- 강의 상태
    status           VARCHAR(20)  NOT NULL,
    rejection_reason TEXT,

    -- BaseAudit 공통 컬럼
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID         NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       UUID         NOT NULL,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,

    CONSTRAINT chk_lecture_price CHECK (price >= 0),
    CONSTRAINT chk_lecture_duration_policy
        CHECK (duration_policy IN ('DAYS_90', 'DAYS_180', 'DAYS_365', 'UNLIMITED')),
    CONSTRAINT chk_lecture_status
        CHECK (status IN ('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'HIDDEN'))
);

-- 조회 가속 인덱스
CREATE INDEX IF NOT EXISTS idx_lecture_status ON lecture.p_lecture (status);
CREATE INDEX IF NOT EXISTS idx_lecture_instructor_id ON lecture.p_lecture (instructor_id);
CREATE INDEX IF NOT EXISTS idx_lecture_category_status ON lecture.p_lecture (category, status);
-- 소프트 삭제 필터링: deleted_at IS NULL 만 인덱스 (부분 인덱스로 용량 절감)
CREATE INDEX IF NOT EXISTS idx_lecture_active_status
    ON lecture.p_lecture (status)
    WHERE deleted_at IS NULL;


-- =====================================================================
-- p_chapter : 챕터
-- =====================================================================
CREATE TABLE IF NOT EXISTS lecture.p_chapter
(
    id               UUID PRIMARY KEY,

    lecture_id       UUID         NOT NULL,

    -- @Embedded ChapterContent
    title            VARCHAR(200) NOT NULL,
    content          TEXT,

    sort_order       INT          NOT NULL,

    -- @Embedded ChapterDuration
    duration_seconds INT          NOT NULL,

    -- BaseAudit 공통 컬럼
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID         NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       UUID         NOT NULL,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,

    CONSTRAINT fk_chapter_lecture
        FOREIGN KEY (lecture_id) REFERENCES lecture.p_lecture (id),

    CONSTRAINT uk_chapter_lecture_sort_order
        UNIQUE (lecture_id, sort_order),

    CONSTRAINT chk_chapter_sort_order CHECK (sort_order >= 1),
    CONSTRAINT chk_chapter_duration_seconds CHECK (duration_seconds >= 1)
);

CREATE INDEX IF NOT EXISTS idx_chapter_lecture_id ON lecture.p_chapter (lecture_id);


-- =====================================================================
-- p_enrollment : 수강 등록
--   - status : RESERVE → ACTIVE → (CANCELED|EXPIRED)
--   - LectureSnapshot 은 등록 당시 강의 정보를 박제
-- =====================================================================
CREATE TABLE IF NOT EXISTS lecture.p_enrollment
(
    id               UUID PRIMARY KEY,

    -- @Embedded LectureSnapshot
    lecture_id       UUID         NOT NULL,
    lecture_title    VARCHAR(225) NOT NULL,
    instructor_id    UUID         NOT NULL,
    instructor_name  VARCHAR(100) NOT NULL,

    student_id       UUID         NOT NULL,
    order_id         UUID,

    status           VARCHAR(20)  NOT NULL,
    duration_policy  VARCHAR(20)  NOT NULL,

    activated_at     TIMESTAMP,
    expires_at       TIMESTAMP,
    last_accessed_at TIMESTAMP,

    -- BaseAudit 공통 컬럼
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID         NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       UUID         NOT NULL,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,

    CONSTRAINT chk_enrollment_status
        CHECK (status IN ('RESERVE', 'ACTIVE', 'CANCELED', 'EXPIRED')),
    CONSTRAINT chk_enrollment_duration_policy
        CHECK (duration_policy IN ('DAYS_90', 'DAYS_180', 'DAYS_365', 'UNLIMITED')),
    CONSTRAINT chk_enrollment_expires_after_activate
        CHECK (expires_at IS NULL OR activated_at IS NULL OR expires_at > activated_at)
);

-- Enrollment.java 의 @Index 와 동일
CREATE INDEX IF NOT EXISTS idx_enrollment_student_status
    ON lecture.p_enrollment (student_id, status);
CREATE INDEX IF NOT EXISTS idx_enrollment_order_id
    ON lecture.p_enrollment (order_id);

-- 자주 쓰는 보조 인덱스
CREATE INDEX IF NOT EXISTS idx_enrollment_lecture_id
    ON lecture.p_enrollment (lecture_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_expires_at
    ON lecture.p_enrollment (expires_at)
    WHERE status = 'ACTIVE';
-- 만료 배치용 부분 인덱스


-- =====================================================================
-- p_outbox : 이벤트 발행 큐 (common-library 의 Outbox 패턴)
--   - 도메인 트랜잭션 안에서 INSERT 됨
--   - OutboxRelayScheduler 가 PENDING 행을 주기적으로 Kafka 로 발행
--   - 컬럼명/타입은 common-library 의 Outbox 엔티티와 1:1 일치 필요
--     → 라이브러리 업데이트 시 본 DDL 도 함께 맞출 것
-- =====================================================================
CREATE TABLE IF NOT EXISTS lecture.p_outbox
(
    message_id     UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id   VARCHAR(100) NOT NULL,
    event_type     VARCHAR(200) NOT NULL,
    topic          VARCHAR(200) NOT NULL,
    payload        TEXT         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- PENDING / SENT / FAILED
    retry_count    INT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at        TIMESTAMP,

    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

-- Relay 스케줄러가 PENDING 만 빠르게 가져갈 수 있도록 부분 인덱스
CREATE INDEX IF NOT EXISTS idx_outbox_pending
    ON lecture.p_outbox (created_at)
    WHERE status = 'PENDING';


-- =====================================================================
-- p_inbox : 수신 멱등성 키 저장 (common-library 의 Inbox 패턴)
--   - InboxAdvice 가 메시지 수신 시 INSERT
--   - PK 충돌 = 이미 처리된 메시지 = 스킵
-- =====================================================================
CREATE TABLE IF NOT EXISTS lecture.p_inbox
(
    message_id    UUID PRIMARY KEY,
    message_group VARCHAR(200) NOT NULL,
    received_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_inbox_message_group
    ON lecture.p_inbox (message_group);


-- =====================================================================
-- 권한 (필요 시 dev/운영에서 부여)
-- =====================================================================
-- GRANT USAGE ON SCHEMA lecture TO goggles;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA lecture TO goggles;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA lecture
--     GRANT ALL PRIVILEGES ON TABLES TO goggles;
