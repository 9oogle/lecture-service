-- =====================================================================
-- lecture-service 시연용 시드 데이터
--
-- 적용 방법 (둘 중 하나) :
--   A) docker-compose 의 postgres 가 처음 부팅될 때 init/ 안의 *.sql 을
--      알파벳 순으로 자동 실행 → 파일명을 02-data.sql 로 두면
--      init.sql 다음에 실행됨
--   B) 수동 실행 :
--        psql -h localhost -U goggles -d goggles -f init/data.sql
--
-- 주의 :
--   - 학생/강사 계정 자체는 user-service 가 관리한다.
--     본 파일에서 사용하는 student/instructor UUID 는 미리 정해둔 값으로,
--     user-service 의 data.sql 에서도 동일한 UUID 로 가입시켜둬야 한다.
--   - 같은 UUID 가 이미 있으면 INSERT 가 실패하므로 재실행 시
--     `docker compose down -v` 로 볼륨을 비우거나 TRUNCATE 하고 다시 넣을 것.
--
-- 시연 시나리오 :
--   강사 3명, 학생 3명, 강의 7개, 챕터 다수, 수강 6건 (다양한 상태)
-- =====================================================================

SET search_path TO lecture, public;


-- =====================================================================
-- 사전 정리 (재실행 안전성)
-- =====================================================================
TRUNCATE TABLE lecture.p_enrollment, lecture.p_chapter, lecture.p_lecture RESTART IDENTITY CASCADE;


-- =====================================================================
-- 가상 사용자 UUID (user-service 와 공유)
-- =====================================================================
--   강사 ───────────────────────────────────────────────────────────
--   김강사 (instructorA) : 11111111-1111-1111-1111-111111111111
--   박강사 (instructorB) : 22222222-2222-2222-2222-222222222222
--   이강사 (instructorC) : 33333333-3333-3333-3333-333333333333
--
--   학생 ───────────────────────────────────────────────────────────
--   학생A (studentA)     : aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
--   학생B (studentB)     : bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
--   학생C (studentC)     : cccccccc-cccc-cccc-cccc-cccccccccccc
--
--   관리자 ─────────────────────────────────────────────────────────
--   관리자 (admin)        : 99999999-9999-9999-9999-999999999999


-- =====================================================================
-- 1) 강의 (p_lecture)
-- =====================================================================
INSERT INTO lecture.p_lecture
(id, instructor_id, instructor_name, category, title, subtitle, description,
 duration_policy, price, status, rejection_reason,
 created_at, created_by, updated_at, updated_by)
VALUES
-- ── 김강사 (instructorA) ─────────────────────────────────────────────
('a1000000-0000-0000-0000-000000000001',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'BACKEND',
 'Spring Boot 완전 정복',
 '기초부터 MSA까지',
 '스프링 부트의 핵심 개념과 실전 패턴을 단계별로 학습합니다.',
 'DAYS_365', 50000, 'PUBLISHED', NULL,
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111'),

('a1000000-0000-0000-0000-000000000002',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'BACKEND',
 'JPA 실전',
 '연관관계와 성능 최적화',
 '실무에서 자주 마주치는 N+1 문제와 해결책을 실습합니다.',
 'DAYS_180', 35000, 'PUBLISHED', NULL,
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111'),

('a1000000-0000-0000-0000-000000000003',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'BACKEND',
 'MSA 입문',
 '마이크로서비스 첫걸음',
 '아직 작성 중인 강의입니다.',
 'DAYS_365', 70000, 'DRAFT', NULL,
 NOW() - INTERVAL '5 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '2 days',  '11111111-1111-1111-1111-111111111111'),

-- ── 박강사 (instructorB) ─────────────────────────────────────────────
('a2000000-0000-0000-0000-000000000001',
 '22222222-2222-2222-2222-222222222222', '김영희',
 'DEVOPS',
 'Docker & Kubernetes',
 '컨테이너부터 오케스트레이션까지',
 '실무 운영 환경에서 사용하는 도커와 쿠버네티스를 다룹니다.',
 'UNLIMITED', 80000, 'PUBLISHED', NULL,
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222'),

('a2000000-0000-0000-0000-000000000002',
 '22222222-2222-2222-2222-222222222222', '김영희',
 'BACKEND',
 'Kafka 마스터하기',
 '이벤트 기반 아키텍처',
 'Kafka 의 내부 구조와 컨슈머 그룹, 파티셔닝 전략을 다룹니다.',
 'DAYS_365', 65000, 'PUBLISHED', NULL,
 NOW() - INTERVAL '90 days',  '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '90 days',  '22222222-2222-2222-2222-222222222222'),

-- ── 이강사 (instructorC) ─────────────────────────────────────────────
('a3000000-0000-0000-0000-000000000001',
 '33333333-3333-3333-3333-333333333333', '이지원',
 'FRONTEND',
 'TypeScript 기초',
 '타입 시스템 입문',
 '자바스크립트 개발자를 위한 타입스크립트 입문 강의.',
 'DAYS_90', 25000, 'PUBLISHED', NULL,
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333'),

('a3000000-0000-0000-0000-000000000002',
 '33333333-3333-3333-3333-333333333333', '이지원',
 'FRONTEND',
 'React 심화',
 '훅과 상태 관리, 성능 최적화',
 '리액트 18 기반의 실전 패턴을 다룹니다. (관리자 승인 대기 중)',
 'DAYS_365', 55000, 'PENDING_REVIEW', NULL,
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333');


-- =====================================================================
-- 2) 챕터 (p_chapter)
--    - PUBLISHED 강의에는 챕터 3~4 개씩
--    - PENDING_REVIEW 강의에도 챕터 필수 (submitForReview 검증)
--    - DRAFT 강의는 챕터 1개만
-- =====================================================================

-- Spring Boot 완전 정복 (a1...001) 챕터 4개
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c1010000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001',
 '1강. 스프링 부트 소개', '스프링 부트가 등장한 배경과 핵심 가치를 다룹니다.', 1, 1200,
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111'),

('c1010000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001',
 '2강. 자동 설정의 원리', '@EnableAutoConfiguration 의 동작 방식을 분석합니다.', 2, 1500,
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111'),

('c1010000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001',
 '3강. 의존성 주입 심화', '생성자 주입과 순환 참조 해결 패턴을 다룹니다.', 3, 1800,
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111'),

('c1010000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001',
 '4강. 테스트 작성하기', 'MockMvc 와 통합 테스트 작성 패턴.', 4, 2100,
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '60 days', '11111111-1111-1111-1111-111111111111');

-- JPA 실전 (a1...002) 챕터 3개
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c1020000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002',
 '1강. JPA 핵심 개념', '영속성 컨텍스트와 1차 캐시.', 1, 1300,
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111'),

('c1020000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000002',
 '2강. 연관관계 매핑', '단방향 vs 양방향, 주인 결정 기준.', 2, 1700,
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111'),

('c1020000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000002',
 '3강. N+1 문제와 해결', 'fetch join, batch size, EntityGraph.', 3, 2200,
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '50 days', '11111111-1111-1111-1111-111111111111');

-- MSA 입문 (a1...003) 챕터 1개 (DRAFT)
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c1030000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000003',
 '1강. MSA 가 필요한 이유', '모놀리식의 한계와 MSA 전환 시 고려사항.', 1, 1400,
 NOW() - INTERVAL '5 days', '11111111-1111-1111-1111-111111111111',
 NOW() - INTERVAL '5 days', '11111111-1111-1111-1111-111111111111');

-- Docker & Kubernetes (a2...001) 챕터 3개
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c2010000-0000-0000-0000-000000000001', 'a2000000-0000-0000-0000-000000000001',
 '1강. 컨테이너의 이해', '도커가 해결하는 문제.', 1, 1600,
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222'),

('c2010000-0000-0000-0000-000000000002', 'a2000000-0000-0000-0000-000000000001',
 '2강. Dockerfile 작성', '계층 구조와 캐시 전략.', 2, 1900,
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222'),

('c2010000-0000-0000-0000-000000000003', 'a2000000-0000-0000-0000-000000000001',
 '3강. Kubernetes 입문', 'Pod, Deployment, Service.', 3, 2400,
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '120 days', '22222222-2222-2222-2222-222222222222');

-- Kafka 마스터하기 (a2...002) 챕터 4개
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c2020000-0000-0000-0000-000000000001', 'a2000000-0000-0000-0000-000000000002',
 '1강. Kafka 아키텍처', '브로커, 토픽, 파티션.', 1, 1500,
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222'),

('c2020000-0000-0000-0000-000000000002', 'a2000000-0000-0000-0000-000000000002',
 '2강. Producer 와 Consumer', '핵심 옵션과 처리 보장.', 2, 1800,
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222'),

('c2020000-0000-0000-0000-000000000003', 'a2000000-0000-0000-0000-000000000002',
 '3강. 컨슈머 그룹 깊게 보기', '리밸런싱, 오프셋 관리.', 3, 2000,
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222'),

('c2020000-0000-0000-0000-000000000004', 'a2000000-0000-0000-0000-000000000002',
 '4강. 운영과 모니터링', 'Lag, ISR, 장애 대응.', 4, 2200,
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222',
 NOW() - INTERVAL '90 days', '22222222-2222-2222-2222-222222222222');

-- TypeScript 기초 (a3...001) 챕터 3개
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c3010000-0000-0000-0000-000000000001', 'a3000000-0000-0000-0000-000000000001',
 '1강. 타입 시스템 개요', '구조적 타이핑 vs 명목적 타이핑.', 1, 1100,
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333'),

('c3010000-0000-0000-0000-000000000002', 'a3000000-0000-0000-0000-000000000001',
 '2강. 인터페이스와 타입 별칭', '언제 무엇을 쓸까.', 2, 1300,
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333'),

('c3010000-0000-0000-0000-000000000003', 'a3000000-0000-0000-0000-000000000001',
 '3강. 제네릭 활용', '재사용 가능한 타입 설계.', 3, 1500,
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '40 days', '33333333-3333-3333-3333-333333333333');

-- React 심화 (a3...002) 챕터 2개 (PENDING_REVIEW)
INSERT INTO lecture.p_chapter
(id, lecture_id, title, content, sort_order, duration_seconds,
 created_at, created_by, updated_at, updated_by)
VALUES
('c3020000-0000-0000-0000-000000000001', 'a3000000-0000-0000-0000-000000000002',
 '1강. 훅의 동작 원리', 'useState, useEffect 의 내부 동작.', 1, 1700,
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333'),

('c3020000-0000-0000-0000-000000000002', 'a3000000-0000-0000-0000-000000000002',
 '2강. 상태 관리 라이브러리 비교', 'Context, Zustand, Redux.', 2, 2100,
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333',
 NOW() - INTERVAL '3 days', '33333333-3333-3333-3333-333333333333');


-- =====================================================================
-- 3) 수강 (p_enrollment)
--    각 학생마다 다른 상태로 데이터를 깔아둔다.
--    LectureSnapshot 은 강의 등록 당시의 정보를 박제하는 것이므로
--    p_lecture 와 동일한 instructor 정보로 채운다.
-- =====================================================================

-- ── 학생A : ACTIVE 1건 (Spring Boot 수강 중), RESERVE 1건 (JPA 결제 대기) ──
INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
-- 학생A: Spring Boot ACTIVE (30일 전 결제, 365일 정책 → 약 11개월 남음, 어제 마지막 수강)
('e1000000-0000-0000-0000-00000000000a',
 'a1000000-0000-0000-0000-000000000001', 'Spring Boot 완전 정복',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'd0000000-0000-0000-0000-00000000000a',  -- 가짜 orderId (시연용)
 'ACTIVE', 'DAYS_365',
 NOW() - INTERVAL '30 days',
 NOW() - INTERVAL '30 days' + INTERVAL '365 days',
 NOW() - INTERVAL '1 days',
 NOW() - INTERVAL '30 days', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 NOW() - INTERVAL '1 days',  'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- 학생A: JPA 실전 RESERVE (★ 시연 포인트: Kafka 이벤트 발행 시 ACTIVE 로 활성화될 대상)
INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
('e1000000-0000-0000-0000-00000000000b',
 'a1000000-0000-0000-0000-000000000002', 'JPA 실전',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 NULL,                                    -- RESERVE 상태라 아직 orderId 없음
 'RESERVE', 'DAYS_180',
 NULL, NULL, NULL,
 NOW() - INTERVAL '5 minutes', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 NOW() - INTERVAL '5 minutes', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');


-- ── 학생B : ACTIVE 1건 (Docker 수강 중), EXPIRED 1건 (Kafka 만료) ──
INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
-- 학생B: Docker UNLIMITED ACTIVE (오래 됐고 어제 봤음)
('e2000000-0000-0000-0000-00000000000a',
 'a2000000-0000-0000-0000-000000000001', 'Docker & Kubernetes',
 '22222222-2222-2222-2222-222222222222', '김영희',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'd0000000-0000-0000-0000-00000000000b',
 'ACTIVE', 'UNLIMITED',
 NOW() - INTERVAL '100 days',
 '9999-12-31 23:59:59',                      -- UNLIMITED 정책
 NOW() - INTERVAL '1 days',
 NOW() - INTERVAL '100 days', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 NOW() - INTERVAL '1 days',   'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
-- 학생B: Kafka EXPIRED (400일 전 결제, 365일 정책 → 만료)
('e2000000-0000-0000-0000-00000000000b',
 'a2000000-0000-0000-0000-000000000002', 'Kafka 마스터하기',
 '22222222-2222-2222-2222-222222222222', '김영희',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'd0000000-0000-0000-0000-00000000000c',
 'EXPIRED', 'DAYS_365',
 NOW() - INTERVAL '400 days',
 NOW() - INTERVAL '400 days' + INTERVAL '365 days',
 NOW() - INTERVAL '50 days',
 NOW() - INTERVAL '400 days', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 NOW() - INTERVAL '35 days',  'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');


-- ── 학생C : ACTIVE 1건 (TypeScript), CANCELED 1건 (Spring Boot 환불) ──
INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
-- 학생C: TypeScript ACTIVE (10일 전 결제, 90일 정책 → 80일 남음)
('e3000000-0000-0000-0000-00000000000a',
 'a3000000-0000-0000-0000-000000000001', 'TypeScript 기초',
 '33333333-3333-3333-3333-333333333333', '이지원',
 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 'd0000000-0000-0000-0000-00000000000d',
 'ACTIVE', 'DAYS_90',
 NOW() - INTERVAL '10 days',
 NOW() - INTERVAL '10 days' + INTERVAL '90 days',
 NOW() - INTERVAL '2 days',
 NOW() - INTERVAL '10 days', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 NOW() - INTERVAL '2 days',  'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO lecture.p_enrollment
(id, lecture_id, lecture_title, instructor_id, instructor_name,
 student_id, order_id, status, duration_policy,
 activated_at, expires_at, last_accessed_at,
 created_at, created_by, updated_at, updated_by)
VALUES
-- 학생C: Spring Boot CANCELED (결제 후 환불)
('e3000000-0000-0000-0000-00000000000b',
 'a1000000-0000-0000-0000-000000000001', 'Spring Boot 완전 정복',
 '11111111-1111-1111-1111-111111111111', '홍길동',
 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 'd0000000-0000-0000-0000-00000000000e',
 'CANCELED', 'DAYS_365',
 NOW() - INTERVAL '20 days',                 -- 한 번 ACTIVE 됐다가
 NOW() - INTERVAL '20 days' + INTERVAL '365 days',
 NULL,
 NOW() - INTERVAL '20 days', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 NOW() - INTERVAL '15 days', 'cccccccc-cccc-cccc-cccc-cccccccccccc');  -- 5일 후 환불


-- =====================================================================
-- 검증 쿼리 (시연 전 점검용 — 주석 처리됨)
-- =====================================================================
-- SELECT title, status, duration_policy, price FROM lecture.p_lecture ORDER BY created_at;
-- SELECT lecture_id, COUNT(*) AS chapter_count FROM lecture.p_chapter GROUP BY lecture_id;
-- SELECT student_id, lecture_title, status, expires_at FROM lecture.p_enrollment ORDER BY student_id, created_at;
