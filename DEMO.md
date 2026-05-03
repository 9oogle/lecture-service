# 시연 준비 & 시나리오 가이드

강의 등록부터 결제 완료 이벤트를 통한 수강 활성화까지 Lecture Service의 주요 흐름을 시연합니다.

이번 시연의 핵심은 **Kafka 주문 완료 이벤트를 수신하여 수강 등록 상태를 `RESERVE`에서 `ACTIVE`로 변경하는 흐름**입니다.

> Phase는 위에서 아래 순서대로 한 번에 실행하는 것을 기준으로 작성되었습니다.
> 중간에 데이터 상태가 변경되므로 특정 Phase만 다시 시연하려면 `docker compose down -v`로 DB 볼륨을 초기화한 뒤 다시 시작하세요.

---

## 0. 시연 도구

| 목적           | 추천 도구                                  |
|--------------|----------------------------------------|
| HTTP API 호출  | Postman 또는 Git Bash curl               |
| Kafka 이벤트 발행 | Kafka UI                               |
| 애플리케이션 로그 확인 | IntelliJ Run Console                   |
| DB 상태 확인     | Git Bash `docker exec psql` 또는 DBeaver |
| 로컬 인프라 실행    | Docker Compose                         |

> `curl | jq` 명령어가 불편하면 Postman으로 API를 호출해도 됩니다.
> Kafka 이벤트 발행은 Postman이 아니라 Kafka UI에서 진행하는 것을 권장합니다.

---

## 1. 사전 준비

### 1-1. `.env` 생성

```bash
cp .env.example .env
```

`.env`에서 로컬 실행 기준으로 아래 값이 설정되어 있는지 확인합니다.

```properties
SPRING_PROFILES_ACTIVE=local
KAFKA_BOOTSTRAP_SERVERS=localhost:9094
```

확인 명령어:

```bash
cat .env | grep SPRING_PROFILES_ACTIVE
cat .env | grep KAFKA_BOOTSTRAP_SERVERS
```

---

### 1-2. 기존 볼륨 초기화

시드 데이터를 새로 넣기 위해 기존 DB 볼륨을 제거합니다.

```bash
docker compose down -v
```

---

### 1-3. 로컬 인프라 실행

```bash
docker compose up -d
```

Postgres 초기화가 끝났는지 확인합니다.

```bash
docker compose logs -f postgres
```

아래와 비슷한 로그가 보이면 `Ctrl + C`로 빠져나옵니다.

```text
database system is ready to accept connections
```

---

### 1-4. 애플리케이션 실행

방법 A. IntelliJ에서 실행

```text
LectureServiceApplication 실행
```

방법 B. Git Bash에서 실행

```bash
./gradlew bootRun
```

실행 로그에서 아래 내용을 확인합니다.

```text
The following 1 profile is active: "local"
Subscribed to topic(s): order.enrollment.completion
```

---

## 2. 참고 사항

### 2-1. local / dev / prod 구분

이번 시연은 **local 프로파일** 기준입니다.

| 프로파일  | 용도                           |
|-------|------------------------------|
| local | 내 PC + docker-compose 로컬 인프라 |
| dev   | 팀 공유 개발 서버 / GCP VM 인프라      |
| prod  | 운영 환경                        |

현재 시연에서 사용하는 주소는 아래와 같습니다.

| 대상                  | 주소                      |
|---------------------|-------------------------|
| Lecture Service     | `http://localhost:9005` |
| Kafka UI            | `http://localhost:8989` |
| Spring Boot → Kafka | `localhost:9094`        |
| Kafka UI → Kafka    | `kafka:9092`            |
| Kafka 컨테이너 내부 CLI   | `localhost:9092`        |

> Spring Boot 애플리케이션은 호스트 PC에서 Kafka에 접속하므로 `localhost:9094`를 사용합니다.
> Kafka 컨테이너 내부에서 토픽을 확인할 때는 컨테이너 내부 주소인 `localhost:9092`를 사용합니다.

---

### 2-2. `jq`가 없을 때

Git Bash에서 `jq`가 없다면 아래 명령어로 설치할 수 있습니다.

```powershell
winget install jqlang.jq
```

설치 후 Git Bash를 다시 열고 확인합니다.

```bash
jq --version
```

`jq`를 사용하지 않고 응답 전체를 확인하려면 `| jq ...` 부분을 제거하면 됩니다.

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/teaching?page=0&size=10" \
  -H "X-User-Id: 11111111-1111-1111-1111-111111111111"
```

Python으로 보기 좋게 출력할 수도 있습니다.

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/teaching?page=0&size=10" \
  -H "X-User-Id: 11111111-1111-1111-1111-111111111111" \
  | python -m json.tool
```

---

### 2-3. ApiResponse 구조

응답 본문은 common-library의 `ApiResponse`로 감싸져 있습니다.

예시:

```json
{
  "code": "200",
  "message": "OK",
  "data": {
    "...": "..."
  }
}
```

따라서 jq 예시는 대부분 `.data.*` 기준으로 작성되어 있습니다.

---

## 3. 시드 데이터 요약

### 3-1. 사용자 UUID

| 역할  | 이름    | UUID                                   |
|-----|-------|----------------------------------------|
| 강사  | 홍길동   | `11111111-1111-1111-1111-111111111111` |
| 강사  | 김영희   | `22222222-2222-2222-2222-222222222222` |
| 강사  | 이지원   | `33333333-3333-3333-3333-333333333333` |
| 학생  | 학생A   | `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` |
| 학생  | 학생B   | `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` |
| 학생  | 학생C   | `cccccccc-cccc-cccc-cccc-cccccccccccc` |
| 관리자 | admin | `99999999-9999-9999-9999-999999999999` |

---

### 3-2. 강의 데이터

| 강의                  | UUID                                   | 상태             | 강사  |
|---------------------|----------------------------------------|----------------|-----|
| Spring Boot 완전 정복   | `a1000000-0000-0000-0000-000000000001` | PUBLISHED      | 홍길동 |
| JPA 실전              | `a1000000-0000-0000-0000-000000000002` | PUBLISHED      | 홍길동 |
| MSA 입문              | `a1000000-0000-0000-0000-000000000003` | DRAFT          | 홍길동 |
| Docker & Kubernetes | `a2000000-0000-0000-0000-000000000001` | PUBLISHED      | 김영희 |
| Kafka 마스터하기         | `a2000000-0000-0000-0000-000000000002` | PUBLISHED      | 김영희 |
| TypeScript 기초       | `a3000000-0000-0000-0000-000000000001` | PUBLISHED      | 이지원 |
| React 심화            | `a3000000-0000-0000-0000-000000000002` | PENDING_REVIEW | 이지원 |

---

### 3-3. 수강 이력 데이터

| 학생  | 강의                  | 상태       | enrollmentId                           | 비고          |
|-----|---------------------|----------|----------------------------------------|-------------|
| 학생A | Spring Boot 완전 정복   | ACTIVE   | DB 조회 필요                               | 수강 중        |
| 학생A | JPA 실전              | RESERVE  | `e1000000-0000-0000-0000-00000000000b` | Kafka 시연 대상 |
| 학생B | Docker & Kubernetes | ACTIVE   | DB 조회 필요                               | 수강 중        |
| 학생B | Kafka 마스터하기         | EXPIRED  | DB 조회 필요                               | 만료 상태       |
| 학생C | TypeScript 기초       | ACTIVE   | DB 조회 필요                               | 수강 중        |
| 학생C | Spring Boot 완전 정복   | CANCELED | DB 조회 필요                               | 취소 상태       |

---

## 4. Phase 1 — 강사 / 관리자 시나리오

### 4-1. 강사: 본인 강의 목록 조회

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/teaching?page=0&size=10" \
  -H "X-User-Id: 11111111-1111-1111-1111-111111111111" \
  | jq '.data.content[] | {title, status}'
```

기대 결과:

```json
{
  "title": "MSA 입문",
  "status": "DRAFT"
}
{
  "title": "JPA 실전",
  "status": "PUBLISHED"
}
{
  "title": "Spring Boot 완전 정복",
  "status": "PUBLISHED"
}
```

설명:

```text
홍길동 강사의 강의 3개가 조회됩니다.
강사 본인은 자신의 DRAFT 강의까지 조회할 수 있습니다.
```

---

### 4-2. 강사: DRAFT 강의 검토 요청

MSA 입문 강의를 `DRAFT`에서 `PENDING_REVIEW`로 변경합니다.

```bash
curl -s -X PATCH "http://localhost:9005/api/v1/lectures/a1000000-0000-0000-0000-000000000003/review-requests" \
  -H "X-User-Id: 11111111-1111-1111-1111-111111111111" \
  -H "X-User-Role: INSTRUCTOR" \
  | jq '.data'
```

기대 결과:

```json
{
  "lectureId": "a1000000-0000-0000-0000-000000000003",
  "status": "PENDING_REVIEW",
  "rejectionReason": null
}
```

설명:

```text
MSA 입문 강의는 챕터가 1개 존재하므로 검토 요청이 가능합니다.
챕터가 0개라면 LECTURE_CHAPTER_REQUIRED 예외가 발생합니다.
```

---

### 4-3. 관리자: PENDING_REVIEW 강의 승인

React 심화 강의를 `PENDING_REVIEW`에서 `PUBLISHED`로 변경합니다.

```bash
curl -s -X PATCH "http://localhost:9005/api/v1/lectures/a3000000-0000-0000-0000-000000000002/status" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 99999999-9999-9999-9999-999999999999" \
  -H "X-User-Role: MASTER" \
  -d '{ "status": "PUBLISHED" }' \
  | jq '.data'
```

설명:

```text
MASTER 권한을 가진 관리자만 강의 상태를 승인 또는 반려할 수 있습니다.
```

---

### 4-4. 관리자: PENDING_REVIEW 강의 반려

방금 검토 요청한 MSA 입문 강의를 다시 `DRAFT`로 반려합니다.

```bash
curl -s -X PATCH "http://localhost:9005/api/v1/lectures/a1000000-0000-0000-0000-000000000003/status" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 99999999-9999-9999-9999-999999999999" \
  -H "X-User-Role: MASTER" \
  -d '{ "status": "DRAFT", "rejectionReason": "내용 보강 필요" }' \
  | jq '.data'
```

설명:

```text
반려 시에는 rejectionReason이 필요합니다.
status=DRAFT인데 rejectionReason이 없으면 LECTURE_REJECTION_REASON_REQUIRED 예외가 발생합니다.
```

---

## 5. Phase 2 — Kafka 주문 완료 이벤트 기반 수강 활성화

학생A의 JPA 실전 수강 등록을 `RESERVE`에서 `ACTIVE`로 변경합니다.

실제 서비스 흐름은 다음과 같습니다.

```text
결제 성공
→ 주문 서비스에서 결제 완료 처리
→ order.enrollment.completion 이벤트 발행
→ Lecture Service Consumer 수신
→ Enrollment RESERVE → ACTIVE 변경
```

이번 시연에서는 주문 서비스를 대신해 Kafka UI에서 이벤트를 직접 발행합니다.

---

### 5-1. 학생A 수강 목록 확인

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/enrolled?page=0&size=10" \
  -H "X-User-Id: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" \
  | jq '.data.content[] | {lectureTitle, status}'
```

기대 결과:

```text
Spring Boot 완전 정복 ACTIVE만 조회됩니다.
JPA 실전은 RESERVE 상태이므로 학생 수강 목록에는 노출되지 않습니다.
```

---

### 5-2. DB에서 RESERVE 상태 확인

```bash
docker exec -it goggles-postgres psql -U goggles -d goggles -c \
  "SELECT id, lecture_title, status, order_id, activated_at, expires_at
   FROM lecture.p_enrollment
   WHERE student_id='aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
   ORDER BY created_at;"
```

기대 결과:

```text
Spring Boot 완전 정복 ACTIVE
JPA 실전 RESERVE
```

Kafka 시연 대상 enrollmentId:

```text
e1000000-0000-0000-0000-00000000000b
```

---

### 5-3. Kafka UI 접속

브라우저에서 접속합니다.

```text
http://localhost:8989
```

이동 경로:

```text
Topics
→ order.enrollment.completion
→ Produce Message
```

---

### 5-4. Kafka 메시지 발행

Value에 아래 JSON을 입력합니다.

```json
{
  "orderId": "d0000000-0000-0000-0000-0000000000ff",
  "enrollmentIds": [
    "e1000000-0000-0000-0000-00000000000b"
  ]
}
```

처음 이벤트 연결만 확인할 때는 Headers를 비워두고 발행해도 됩니다.

멱등성까지 확인하려면 Headers에 `message_id`를 추가합니다.

Kafka UI에서 Key/Value 방식으로 Header를 입력할 수 있다면 다음처럼 입력합니다.

| Key          | Value                                  |
|--------------|----------------------------------------|
| `message_id` | `00000000-0000-0000-0000-000000000001` |

Headers를 JSON 형식으로 입력해야 하는 UI라면 아래처럼 입력합니다.

```json
[
  {
    "key": "message_id",
    "value": "00000000-0000-0000-0000-000000000001"
  }
]
```

> Kafka UI 버전에 따라 Headers 입력 방식이 다를 수 있습니다.
> Header 입력에서 오류가 나면 일단 Headers 없이 메시지를 발행해 이벤트 연결 여부를 먼저 확인합니다.

---

### 5-5. 애플리케이션 로그 확인

IntelliJ Run Console에서 아래 로그를 확인합니다.

```text
[Kafka] Received order.enrollment.completion | partition=0, offset=...
Enrollment completion processed. orderId=d0000000-..., count=1
```

이 로그가 찍히면 아래 흐름이 정상 동작한 것입니다.

```text
Kafka Consumer 수신
→ payload 파싱
→ LectureEnrollmentCompleteCommand 생성
→ EnrollmentCommandService.complete()
→ Enrollment 상태 ACTIVE 변경
```

---

### 5-6. DB에서 ACTIVE 변경 확인

```bash
docker exec -it goggles-postgres psql -U goggles -d goggles -c \
  "SELECT id, order_id, lecture_title, status, activated_at, expires_at
   FROM lecture.p_enrollment
   WHERE id='e1000000-0000-0000-0000-00000000000b';"
```

기대 결과:

```text
status       = ACTIVE
order_id     = d0000000-0000-0000-0000-0000000000ff
activated_at = 값 있음
expires_at   = 값 있음
```

---

### 5-7. 학생A 수강 목록 재조회

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/enrolled?page=0&size=10&sort=recentActivated" \
  -H "X-User-Id: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" \
  | jq '.data.content[] | {lectureTitle, status, activatedAt}'
```

기대 결과:

```text
JPA 실전이 ACTIVE 상태로 조회됩니다.
recentActivated 정렬 기준으로 최근 활성화된 강의가 상단에 노출됩니다.
```

---

### 5-8. 멱등성 검증

같은 페이로드와 같은 `message_id` Header로 한 번 더 발행합니다.

기대 결과:

```text
동일 message_id 재수신
→ InboxAdvice에서 중복 이벤트로 판단
→ 비즈니스 로직 재실행 방지
```

주의:

```text
message_id를 다른 UUID로 바꾸면 새로운 메시지로 인식됩니다.
이 경우 이미 ACTIVE 상태인 enrollment를 다시 활성화하려고 하므로
도메인에서 InvalidEnrollmentStatusException이 발생하는 것이 정상입니다.
```

Inbox 테이블 확인:

```bash
docker exec -it goggles-postgres psql -U goggles -d goggles -c \
  "SELECT *
   FROM lecture.p_inbox
   ORDER BY received_at DESC
   LIMIT 5;"
```

---

## 6. Phase 3 — 내부 API 시연

주문 서비스가 Lecture Service로 호출하는 내부 API 흐름을 시연합니다.

> `/internal/**` API는 실제 운영에서는 Gateway 외부에 직접 노출되지 않는 내부 API로 관리되어야 합니다.

---

### 6-1. 수강 예약 생성

학생B가 JPA 실전과 Spring Boot 완전 정복을 예약 수강 상태로 생성하는 흐름입니다.

```bash
curl -s -X POST "http://localhost:9005/internal/v1/lectures-enrollment" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "productIds": [
      "a1000000-0000-0000-0000-000000000002",
      "a1000000-0000-0000-0000-000000000001"
    ]
  }' \
  | jq '.data'
```

응답에서 `enrollmentId`를 복사해둡니다.

예시:

```json
[
  {
    "enrollmentId": "<신규 UUID 1>",
    "productId": "a1000000-0000-0000-0000-000000000002",
    "productName": "JPA 실전",
    "productPrice": 35000,
    "instructorId": "11111111-1111-1111-1111-111111111111",
    "instructorName": "홍길동"
  },
  {
    "enrollmentId": "<신규 UUID 2>",
    "productId": "a1000000-0000-0000-0000-000000000001",
    "productName": "Spring Boot 완전 정복",
    "productPrice": 50000,
    "instructorId": "11111111-1111-1111-1111-111111111111",
    "instructorName": "홍길동"
  }
]
```

---

### 6-2. 중복 수강 예약 실패 케이스

이미 ACTIVE 상태로 보유한 강의를 다시 예약하려고 하면 예외가 발생합니다.

```bash
curl -s -X POST "http://localhost:9005/internal/v1/lectures-enrollment" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "productIds": [
      "a2000000-0000-0000-0000-000000000001"
    ]
  }'
```

기대 결과:

```text
400 Bad Request
DUPLICATE_ENROLLMENT 관련 예외
```

---

### 6-3. 수강 취소

6-1에서 생성된 RESERVE 상태의 enrollmentId 중 하나를 취소합니다.

```bash
curl -s -X POST "http://localhost:9005/internal/v1/lectures-enrollment/cancellation" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "enrollmentIds": [
      "<6-1 응답의 enrollmentId 1>"
    ]
  }'
```

기대 결과:

```text
200 OK
```

설명:

```text
RESERVE 또는 ACTIVE 상태의 수강 등록만 취소할 수 있습니다.
본인 소유 enrollment가 아니면 EnrollmentNotOwnedException이 발생합니다.
```

---

## 7. Phase 4 — 조회 시나리오

### 7-1. 학생B 만료 강의 조회

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/enrolled?status=EXPIRED" \
  -H "X-User-Id: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" \
  | jq '.data.content[] | {lectureTitle, status, expiresAt}'
```

기대 결과:

```text
Kafka 마스터하기 EXPIRED 상태 강의가 조회됩니다.
```

---

### 7-2. 학생B RESERVE 상태 DB 확인

RESERVE 상태는 학생 수강 목록 API에는 노출되지 않으므로 DB에서 확인합니다.

```bash
docker exec -it goggles-postgres psql -U goggles -d goggles -c \
  "SELECT lecture_title, status
   FROM lecture.p_enrollment
   WHERE student_id='bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
   ORDER BY created_at DESC;"
```

---

### 7-3. RESERVE / CANCELED 검색 제한 확인

학생 수강 목록 API에서 `RESERVE`, `CANCELED` 상태 검색은 허용하지 않습니다.

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/enrolled?status=RESERVE" \
  -H "X-User-Id: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
```

기대 결과:

```text
400 Bad Request
ENROLLMENT_STATUS_NOT_SEARCHABLE
```

---

## 8. 자주 막히는 포인트

| 증상                                              | 원인 / 해결                                                                                 |                             |
|-------------------------------------------------|-----------------------------------------------------------------------------------------|-----------------------------|
 `jq: command not found`                         | jq가 설치되지 않은 상태입니다. `winget install jqlang.jq`로 설치하거나 `\| jq ...`를 제거하고 응답 전체를 확인합니다.    |                             | jq가 설치되지 않은 상태입니다. `winget install jqlang.jq`로 설치하거나 `                                  | jq ...`를 제거하고 응답 전체를 확인합니다. |
| Git Bash에서 `>` 프롬프트가 뜸                          | 따옴표가 닫히지 않은 상태입니다. `Ctrl + C`로 빠져나온 뒤 명령어를 다시 입력합니다.                                    |                             |
| init SQL이 실행되지 않음                               | 기존 DB 볼륨이 남아있는 상태입니다. `docker compose down -v` 후 다시 실행합니다.                              |                             |
| `ddl-auto: validate`에서 부팅 실패                    | 엔티티와 init SQL이 불일치한 상태입니다. init SQL을 수정하거나 로컬에서 임시로 `ddl-auto: update`로 확인합니다.          |                             |
| Kafka Consumer가 메시지를 받지 않음                      | 토픽 이름이 `order.enrollment.completion`인지 확인하고, 앱 로그에 `Subscribed to topic(s)`가 있는지 확인합니다. |                             |
| Kafka 연결 WARN이 반복됨                              | Spring Boot는 `localhost:9094`로 Kafka에 붙어야 합니다. `.env`와 `application-local.yaml`을 확인합니다. |                             |
| Kafka UI Header 입력 오류                           | Header 형식이 UI 버전과 다를 수 있습니다. 일단 Headers 없이 Value만 보내 이벤트 연결을 먼저 확인합니다.                  |                             |
| 멱등성 시연이 되지 않음                                   | 같은 `message_id` Header로 두 번 발행해야 Inbox 기반 dedup을 확인할 수 있습니다.                            |                             |
| `enrolled` 응답에 RESERVE/CANCELED가 안 보임           | 정상입니다. 학생 수강 목록은 ACTIVE/EXPIRED 상태만 검색 대상으로 봅니다.                                        |                             |
| `?status=RESERVE` 호출 시 400 발생                   | 정상입니다. `ENROLLMENT_STATUS_NOT_SEARCHABLE` 예외가 발생해야 합니다.                                 |                             |
| 검토 요청 시 `LECTURE_CHAPTER_REQUIRED` 발생           | 챕터가 없는 DRAFT 강의는 검토 요청할 수 없습니다.                                                         |                             |
| 관리자 반려 시 `LECTURE_REJECTION_REASON_REQUIRED` 발생 | `status=DRAFT`로 반려할 때는 `rejectionReason`이 필요합니다.                                        |                             |

---

## 9. 시연 직전 30초 체크

### 9-1. DB 시드 데이터 확인

```bash
docker exec -it goggles-postgres psql -U goggles -d goggles -c \
  "SELECT student_id, lecture_title, status
   FROM lecture.p_enrollment
   ORDER BY student_id, created_at;"
```

---

### 9-2. Kafka 토픽 확인

먼저 Kafka 컨테이너명을 확인합니다.

```bash
docker ps
```

컨테이너명이 `goggles-kafka`라면:

```bash
docker exec -it goggles-kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list
```

컨테이너명이 `lecture-kafka`라면:

```bash
docker exec -it lecture-kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list
```

> 위 명령은 Kafka 컨테이너 내부에서 실행하므로 `localhost:9092`를 사용합니다.
> Spring Boot 앱에서 Kafka에 접속할 때는 `localhost:9094`를 사용합니다.

---

### 9-3. 앱 헬스체크

```bash
curl -s http://localhost:9005/actuator/health
```

기대 결과:

```json
{
  "status": "UP"
}
```

---

### 9-4. 학생A enrolled 확인

```bash
curl -s "http://localhost:9005/api/v1/me/lectures/enrolled" \
  -H "X-User-Id: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" \
  | jq '.data.totalElements'
```

기대 결과:

```text
1
```

---

### 9-5. Kafka UI 접속

Windows Git Bash 기준:

```bash
explorer.exe http://localhost:8989
```

또는 브라우저 주소창에 직접 입력합니다.

```text
http://localhost:8989
```
