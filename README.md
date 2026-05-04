# lecture-service

> Goggles Edu의 강의 등록 / 조회 / 수강 관리 마이크로서비스.

학생 수강 활성화는 주문 서비스가 결제 성공을 확인한 뒤 발행하는 `order.enrollment.completion` Kafka 이벤트를 수신해 **RESERVE → ACTIVE** 로 전환합니다.

---

## 🚀 로컬 실행 가이드

`local` 프로파일 기준 — 로컬 머신에서 docker-compose 로 띄운 인프라(PostgreSQL / Redis / Kafka / Zipkin) 에 붙어 동작합니다. GCP 환경(Kafka 3브로커
클러스터, Redis Sentinel HA) 연동은 `dev` 프로파일에서 별도로 처리됩니다.

### 사전 요구 사항

| 도구                           | 버전                 | 비고                     |
|------------------------------|--------------------|------------------------|
| JDK                          | 21                 |                        |
| Docker Desktop               | 최신                 | docker-compose v2 포함   |
| IntelliJ IDEA                | 2024.x 이상 권장       |                        |
| GitHub Personal Access Token | `read:packages` 권한 | `common-library` 다운로드용 |

> ⚠️ IntelliJ 인코딩이 UTF-8 인지 반드시 확인. (Settings → Editor → File Encodings → 모두 UTF-8) Windows CP949 로 되어있으면 한글 문자열이 깨지면서
> Spotless 가 실패합니다.

---

### 1. 최초 1회 — 프로젝트 셋업

#### 1-1. GitHub Packages 인증 설정

`common-library` 는 GitHub Packages 에 배포되어 있어 PAT 인증이 필요합니다.

```bash
# 1) GitHub → Settings → Developer settings → Personal access tokens (classic)
#    → Generate new token → 권한: read:packages 체크

# 2) ~/.gradle/gradle.properties 에 등록 (없으면 새로 생성)
GitHubPackagesUsername=깃헙_아이디
GitHubPackagesPassword=ghp_xxxxxxxxxxxxxxxxxxxx
```

> 🚫 `gradle.properties` 는 **프로젝트 내부에 두지 말 것.** 반드시 `~/.gradle/` 홈 디렉토리에. 프로젝트에 두면 그대로 git 에 올라갑니다.

#### 1-2. 레포 클론 및 환경 변수 설정

```bash
git clone <lecture-service-repo-url>
cd lecture-service

# .env.example 을 복사해 본인 .env 생성
cp .env.example .env
```

`.env` 의 기본값(`SPRING_PROFILES_ACTIVE=local`, DB 계정 `goggles/goggles`, Kafka `localhost:9094`)은 docker-compose 기본 설정과
동일하므로 **로컬 실행에서는 그대로 두면 됩니다.**

#### 1-3. 빌드 검증

```bash
./gradlew build
```

처음에는 의존성 다운로드 때문에 시간이 좀 걸립니다. `BUILD SUCCESSFUL` 이 뜨면 환경 설정 완료.

---

### 2. 매번 개발 시작할 때

#### 2-1. 인프라 컨테이너 기동

```bash
docker compose up -d
```

기동되는 컨테이너:

| 컨테이너               | 호스트 포트 | 용도                                                                            |
|--------------------|--------|-------------------------------------------------------------------------------|
| `goggles-postgres` | 5432   | PostgreSQL — 첫 기동 시 `init/01-init.sql` 로 schema, `02-data.sql` 로 시드 데이터 자동 주입 |
| `goggles-redis`    | 6379   | Redis 단일 노드                                                                   |
| `lecture-kafka`    | 9094   | Kafka KRaft 단일 브로커 (zookeeper 없음, 외부 노출 포트 9094)                              |
| `lecture-kafka-ui` | 8989   | Kafka GUI — http://localhost:8989                                             |
| `goggles-zipkin`   | 9411   | 분산 추적 UI — http://localhost:9411                                              |

> Healthcheck 가 정의된 건 `goggles-postgres` 와 `goggles-redis` 둘 뿐입니다. 나머지는 단순히 `Up` 상태이면 정상.

상태 확인:

```bash
docker compose ps
```

> 🕐 Kafka 는 KRaft 모드 첫 부팅 시 메타데이터 디렉토리 초기화로 30초 정도 걸립니다. `docker compose logs -f kafka` 로 `Kafka Server started` 확인 후 다음
> 단계로.

#### 2-2. lecture-service 실행

**옵션 A. 터미널**

```bash
./gradlew bootRun
```

**옵션 B. IntelliJ**

1. `LectureServiceApplication.java` 우클릭 → Run
2. Run Configuration → Active profiles → `local` (또는 환경변수 `SPRING_PROFILES_ACTIVE=local`)

#### 2-3. 기동 검증

| 확인 항목              | URL / 명령                                                               | 기대 결과               |
|--------------------|------------------------------------------------------------------------|---------------------|
| Swagger UI         | http://localhost:9005/swagger-ui.html                                  | API 목록 표시           |
| Health Check       | `curl http://localhost:9005/actuator/health`                           | `{"status":"UP"}`   |
| Prometheus Metrics | `curl http://localhost:9005/actuator/prometheus`                       | JVM/HTTP 메트릭 출력     |
| DB Schema          | `docker exec -it goggles-postgres psql -U goggles -d goggles -c "\dn"` | `lecture` schema 보임 |
| 시드 데이터             | `... -c "SELECT count(*) FROM lecture.p_lecture;"`                     | `7`                 |
| Kafka 토픽           | http://localhost:8989 → Topics                                         | (메시지 발행 후 자동 생성)    |

기동 로그에서 보이면 정상:

```text
Started LectureServiceApplication in X.XXX seconds
HikariPool-1 - Start completed.
[Consumer ...] Setting newly assigned partitions: order.enrollment.completion-0
```

---

### 3. 주문 완료 → 수강 활성화 흐름 시연

본 서비스의 핵심 기능. 전체 흐름은 다음과 같다 :

```text
[사용자] 결제 클릭
  └─▶ [주문 서비스] 구매자 검증 / 강의 등록 예약(RESERVE) / 할인 계산 / 주문 생성
        └─▶ [결제 서비스] 결제 처리 (Kafka 비동기)
              └─▶ [주문 서비스] 결제 성공 이벤트 수신, 주문 = 결제완료 처리
                    └─▶ Kafka publish: order.enrollment.completion  ──────┐
                                                                          │
                                                                          ▼
                                              [강의 서비스(본 서비스)] 가 수신
                                              → enrollment RESERVE → ACTIVE
```

> 즉 본 서비스가 받는 이벤트는 결제 서비스가 직접 보내는 게 아니라, **주문 서비스가 결제 성공을 확인한 뒤 "수강 등록을 완료시켜 달라"는 의미로 발행하는 통지**이다.

자세한 시연 시나리오는 [`DEMO.md`](DEMO.md) 참고.

요약:

1. 학생A 수강 목록 DB 직접 조회 → JPA 실전이 RESERVE 상태로 보임
2. Kafka UI(http://localhost:8989) → Topics → `order.enrollment.completion` → **Produce Message**
    - **Headers** 탭에 `message_id` (UUID) 추가 — `@IdempotentConsumer` 가 멱등성 키로 사용
    - Value:
      ```json
      { "orderId": "d0000000-0000-0000-0000-0000000000ff",
        "enrollmentIds": ["e1000000-0000-0000-0000-00000000000b"] }
      ```
3. lecture-service 콘솔에 `[Kafka] Received order.enrollment.completion ...` 출력
4. 학생A 수강 목록 다시 조회 → JPA 실전이 ACTIVE 로 전환됨
5. **같은 `message_id` 헤더로** 한 번 더 발행 → `skipped(idempotent)=1` 로그 (멱등성 검증)

---

### 4. 종료

```bash
# 앱 종료
Ctrl + C   # bootRun 또는 IntelliJ Stop 버튼

# 인프라 컨테이너 정지 (데이터는 유지)
docker compose down

# 데이터까지 완전 삭제 (init.sql / data.sql 재실행 필요할 때)
docker compose down -v
```

---

## 🛠️ 자주 마주치는 문제

### `Could not resolve com.goggles:common-library`

→ GitHub PAT 미설정. 1-1 단계 다시 확인.

### `Connection refused: localhost:5432`

→ docker compose 가 안 떴거나 postgres healthcheck 실패. `docker compose ps` 로 상태 확인 후 `docker compose logs postgres`.

### `Schema "lecture" does not exist`

→ `init/01-init.sql` 이 실행되지 않은 상태. 한 번이라도 띄운 적이 있다면 볼륨이 이미 있어서 init 스크립트가 무시됨. 아래 명령으로 초기화:

```bash
docker compose down -v
docker compose up -d
```

### `Connection to node -1 (localhost/127.0.0.1:9094) could not be established`

→ Kafka 가 아직 안 떴거나 헬스체크 실패. KRaft 모드는 첫 부팅 시 메타데이터 디렉토리 초기화로 30초 정도 걸림. `docker compose logs kafka` 로
`Kafka Server started` 확인 후 앱 재기동.

### Kafka UI 에서 메시지 발행했는데 멱등성 시연이 안 됨

→ `message_id` Header 누락. Kafka UI **Produce Message** 화면의 Headers 탭에 `message_id: <UUID>` 를 명시해야 `@IdempotentConsumer`
의 dedup 이 동작합니다. 헤더 다른 두 메시지는 다른 메시지로 인식됨.

### Spotless 가 한글 파일에서 실패

→ IntelliJ 인코딩이 CP949 로 잡혀있을 가능성. Settings → Editor → File Encodings → Project/IDE/Default 모두 `UTF-8` 로 변경.

### `Failed to load ApplicationContext` + Config Server 관련 에러

→ `dev` 프로파일로 떴는데 Config Server 가 안 켜져있을 때 발생. 로컬에서는 `SPRING_PROFILES_ACTIVE=local` 확인 (또는 미지정).

---

## 🌐 Dev 프로파일 (GCP 인프라 연동)

GCP VM 위의 공유 인프라(Kafka 3브로커 클러스터, Redis Sentinel HA, Eureka, Config Server) 에 붙을 때 사용.

```bash
SPRING_PROFILES_ACTIVE=dev \
CONFIG_SERVER_URL=http://<CONFIG_SERVER_HOST>:9000 \
./gradlew bootRun
```

Config Server 에서 가져오는 키:

```properties
KAFKA_BOOTSTRAP_SERVERS=<KAFKA_1_INTERNAL_IP>:9092,<KAFKA_2_INTERNAL_IP>:9092,<KAFKA_3_INTERNAL_IP>:9092
REDIS_SENTINEL_1/2/3=<REDIS_VM_INTERNAL_IP>:26379
REDIS_PASSWORD=****
DB_HOST / DB_PORT / DB_NAME / DB_USERNAME / DB_PASSWORD
EUREKA_DEFAULT_ZONE=http://<EUREKA_VM_1>:9001/eureka/,http://<EUREKA_VM_2>:9002/eureka/
MONITORING_HOST=<MONITORING_VM_EXTERNAL_IP>
```

> Prometheus 스크레이핑 대상으로 등록하려면 `monitoring-vm/.env` 와 `prometheus.yml.tmpl` 에 본 서비스 호스트(예:
`LECTURE_SERVICE_HOST=10.178.0.x`) 를 추가하고 monitoring VM 을 재기동. 자세한 내용은 별도 service-monitoring-guide 참고.

---

## 📂 프로젝트 구조

```text
lecture-service/
├── docker-compose.yml          # 로컬 인프라 (postgres + redis + kafka + kafka-ui + zipkin)
├── init/                       # postgres 최초 기동 시 자동 실행
│   ├── 01-init.sql             # 스키마/테이블 생성
│   └── 02-data.sql             # 시연용 시드 데이터
├── .env.example                # 환경변수 템플릿 (commit)
├── .env                        # 본인용 환경변수 (gitignore)
├── DEMO.md                     # 시연 시나리오
├── build.gradle
└── src/main/
    ├── java/com/goggles/lecture_service/
    │   ├── domain/             # 도메인 계층 (Lecture, Chapter, Enrollment 애그리거트)
    │   ├── application/        # 응용 계층 (Command/Query 서비스 + DTO)
    │   ├── infrastructure/     # 인프라 계층
    │   │   ├── enrollment/     #   - Repository 구현체
    │   │   ├── lecture/
    │   │   └── messaging/      #   - Kafka Consumer (주문 완료 → 수강 활성화)
    │   └── presentation/       # 표현 계층 (Controller, Request/Response DTO)
    └── resources/
        ├── application.yaml    # 공통 설정 (Actuator/Prometheus/Zipkin 포함)
        ├── application-local.yaml
        ├── application-dev.yaml
        └── application-prod.yaml
```

---

## 📌 도메인/이벤트 메모

### 강의 상태 흐름

`DRAFT → PENDING_REVIEW → PUBLISHED` (또는 PENDING_REVIEW → DRAFT 반려)
`PUBLISHED → HIDDEN` (관리자 숨김)

### 수강 상태 흐름

`RESERVE` (주문 생성 시 강의 서비스가 예약) → `ACTIVE` (주문 서비스의 수강 등록 완료 이벤트 수신) → `EXPIRED` (수강기간 만료, 배치 처리)
`RESERVE | ACTIVE → CANCELED` (주문 취소 / 환불)

### Kafka 토픽

| Topic                         | 방향 | Group ID                                | 설명                                                   |
|-------------------------------|----|-----------------------------------------|------------------------------------------------------|
| `order.enrollment.completion` | 수신 | `lecture-service.enrollment-completion` | 주문 서비스가 결제 성공을 확인하고 발행 → enrollment RESERVE → ACTIVE |