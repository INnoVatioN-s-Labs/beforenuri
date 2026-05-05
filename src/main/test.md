# 레트로 PC통신 스타일 익명 채팅 서비스 아키텍처 설계서

## 1. 문서 목적

본 문서는 레트로 PC통신 감성을 살린 웹 기반 익명 채팅 서비스 `t4lk`의 아키텍처 초안입니다.
현재 프로젝트는 Spring Boot 초기 상태이므로, 이 문서는 향후 구현을 위한 기준 문서 역할을 합니다.

목표는 다음과 같습니다.

1. 익명성이 유지되는 실시간 채팅 서비스 제공
2. 방 기반 대화 구조와 과거 메시지 조회 지원
3. 초기 소규모 트래픽에 대응하면서도 이후 확장이 가능한 구조 확보

---

## 2. 서비스 요구사항

### 2.1. 핵심 요구사항

1. 사용자는 회원가입 없이 익명으로 입장할 수 있어야 한다.
2. 사용자는 방 번호를 선택해 채팅방에 입장할 수 있어야 한다.
3. 사용자는 입장 즉시 최근 대화 내역을 확인할 수 있어야 한다.
4. 사용자는 실시간으로 메시지를 송수신할 수 있어야 한다.
5. 사용자는 스크롤을 올려 이전 대화 내역을 추가로 조회할 수 있어야 한다.
6. 관리자(Sysop)는 고정된 채팅방을 생성하고 활성 여부를 제어할 수 있어야 한다.

### 2.2. 비기능 요구사항

1. 초기 동시 접속자 수는 100명 이내를 기준으로 한다.
2. 텍스트 중심 서비스로 시작하며 이미지, 파일 전송은 제외한다.
3. 메시지 유실 가능성을 낮추기 위해 실시간 전송과 저장을 함께 처리한다.
4. 구조는 추후 로그인, 1:1 대화, 관리자 기능 확장을 고려해 설계한다.

---

## 3. 전체 아키텍처

### 3.1. 시스템 구성

- Frontend: React + Vite
- Backend: Spring Boot 4.x, Java 17
- Real-time: Spring WebSocket + STOMP
- RDB: MySQL 또는 PostgreSQL
- Message Store: MongoDB
- Optional Infra: Redis

### 3.2. 구성 의도

RDB는 방, 카테고리, 닉네임 사전처럼 구조화된 운영 데이터를 관리합니다.
MongoDB는 양이 빠르게 늘어나는 채팅 메시지를 저장합니다.
실시간 송수신은 WebSocket/STOMP로 처리하고, 과거 메시지 조회는 HTTP API로 분리합니다.

이 구조는 다음 장점이 있습니다.

1. 운영성 데이터와 대량 메시지 데이터를 분리할 수 있다.
2. 실시간 전송과 이력 조회를 서로 다른 방식으로 최적화할 수 있다.
3. 이후 Redis 기반 브로커 확장이나 다중 인스턴스 확장이 쉽다.

---

## 4. 기술 스택

### 4.1. Frontend

- React 18
- Vite
- Vanilla CSS 또는 CSS Modules
- `@stomp/stompjs`
- `sockjs-client`
- Axios

추가 상태 관리가 필요해지면 Zustand 도입을 고려합니다.

### 4.2. Backend

- Spring Boot 4.x
- Spring Web
- Spring WebSocket
- Spring Data JPA
- Spring Data MongoDB
- Validation

### 4.3. 향후 도입 후보

- Redis: 세션 관리, Pub/Sub 브로커 확장
- Spring Security: 로그인 기능 도입 시 적용
- Testcontainers: RDB, MongoDB 통합 테스트 자동화

---

## 5. 데이터 모델 설계

### 5.1. RDB 테이블

#### `chat_category`

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| `id` | BIGINT | 카테고리 PK |
| `name` | VARCHAR(100) | 카테고리명 |
| `sort_order` | INT | 정렬 순서 |

#### `chat_room`

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| `id` | BIGINT | 방 번호이자 PK |
| `category_id` | BIGINT | 카테고리 FK |
| `title` | VARCHAR(200) | 방 제목 |
| `description` | VARCHAR(500) | 방 설명 |
| `is_active` | BOOLEAN | 활성 여부 |
| `created_at` | TIMESTAMP | 생성 시각 |
| `updated_at` | TIMESTAMP | 수정 시각 |

#### `nickname_word`

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| `id` | BIGINT | PK |
| `word_type` | VARCHAR(30) | `ADJECTIVE`, `NOUN` |
| `word` | VARCHAR(100) | 닉네임 구성 단어 |
| `is_active` | BOOLEAN | 사용 가능 여부 |

### 5.2. MongoDB 컬렉션x

#### `chat_messages`

```json
{
  "_id": "ObjectId('...')",
  "roomId": 14,
  "senderType": "ANONYMOUS",
  "senderName": "명예로운 팬티_192.168",
  "content": "안녕하세요.",
  "messageType": "CHAT",
  "createdAt": "2026-05-05T13:45:00.000Z"
}
```

### 5.3. 인덱스 전략

`chat_messages`에는 아래 복합 인덱스를 둡니다.

```text
{ roomId: 1, createdAt: -1 }
```

목적은 다음과 같습니다.

1. 방 단위 최근 메시지 조회 최적화
2. 커서 기반 과거 메시지 조회 최적화

---

## 6. 닉네임 및 익명 식별 정책

### 6.1. 기본 원칙

사용자는 회원가입 없이 입장하지만, 같은 화면 안에서 식별 가능한 표시는 필요합니다.

표시 형식 예시는 다음과 같습니다.

```text
명예로운 팬티_192.168
```

### 6.2. 생성 방식

1. 형용사 1개 + 명사 1개를 닉네임 사전에서 랜덤 선택
2. 접속 IP 일부를 마스킹해 suffix 생성
3. 조합 결과를 클라이언트에 전달

### 6.3. 주의사항

실제 원본 IP를 그대로 저장하거나 노출하지 않습니다.
표시용 suffix는 서비스 감성을 위한 식별자일 뿐, 개인정보 취급 범위를 최소화해야 합니다.

---

## 7. API 및 WebSocket 설계

### 7.1. HTTP API

### 익명 입장 토큰 발급

`POST /api/session/anonymous`

응답 예시:

```json
{
  "sessionToken": "temporary-token",
  "displayName": "명예로운 팬티_192.168"
}
```

### 방 목록 조회

`GET /api/rooms`

### 방 상세 조회

`GET /api/rooms/{roomId}`

### 최근 메시지 조회

`GET /api/rooms/{roomId}/messages?cursor=2026-05-05T13:45:00.000Z&size=50`

응답 예시:

```json
{
  "items": [],
  "nextCursor": "2026-05-05T13:30:00.000Z",
  "hasNext": true
}
```

### 7.2. WebSocket/STOMP

### 연결 엔드포인트

`/ws`

### 구독 경로

`/topic/rooms/{roomId}`

### 발행 경로

`/app/rooms/{roomId}/chat`

### 메시지 예시

```json
{
  "content": "반갑습니다."
}
```

### 서버 브로드캐스트 예시

```json
{
  "roomId": 14,
  "senderName": "명예로운 팬티_192.168",
  "messageType": "CHAT",
  "content": "반갑습니다.",
  "createdAt": "2026-05-05T13:45:00.000Z"
}
```

---

## 8. 주요 처리 흐름

### 8.1. 최초 접속

1. 클라이언트가 익명 세션 발급 API 호출
2. 서버가 닉네임 생성 후 임시 토큰 반환
3. 클라이언트가 토큰을 보관
4. 클라이언트가 방 목록을 조회

### 8.2. 채팅방 입장

1. 클라이언트가 방 상세 및 최근 메시지 조회
2. 서버가 방 활성 여부를 RDB에서 검증
3. 서버가 MongoDB에서 최근 메시지 조회
4. 클라이언트가 WebSocket 연결 후 해당 방 구독
5. 서버가 입장 시스템 메시지를 발행하고 저장

### 8.3. 메시지 전송

1. 클라이언트가 STOMP 발행
2. 서버가 토큰 및 방 상태 검증
3. 서버가 메시지 객체 생성
4. 서버가 MongoDB에 저장
5. 서버가 구독자들에게 브로드캐스트

### 8.4. 과거 메시지 조회

1. 클라이언트가 현재 가장 오래된 메시지 시각을 cursor로 전달
2. 서버가 해당 시각 이전 메시지 `size`개 조회
3. 응답으로 메시지 목록과 다음 cursor 반환

---

## 9. 백엔드 패키지 구조 제안

```text
com.toyproject.t4lk
├── common
├── config
├── room
│   ├── controller
│   ├── service
│   ├── domain
│   └── repository
├── chat
│   ├── controller
│   ├── websocket
│   ├── service
│   ├── document
│   └── repository
├── session
│   ├── controller
│   ├── service
│   └── dto
└── admin
```

패키지는 기능 기준으로 나누고, 공통 유틸은 `common`, 설정은 `config`로 분리합니다.

---

## 10. 예외 처리 및 운영 정책

### 10.1. 예외 처리

다음 상황에 대한 공통 응답 규격이 필요합니다.

1. 존재하지 않는 방 번호
2. 비활성화된 방 접근
3. 잘못된 세션 토큰
4. 빈 메시지 또는 길이 초과 메시지
5. WebSocket 연결 실패

표준 에러 응답 예시:

```json
{
  "code": "ROOM_NOT_FOUND",
  "message": "존재하지 않는 채팅방입니다."
}
```

### 10.2. 메시지 제한 정책

1. 메시지 최대 길이 제한: 예시 300자
2. 연속 전송 제한: 예시 1초당 3회 이하
3. 금칙어 또는 신고 기능은 후속 단계에서 추가

---

## 11. 보안 고려사항

1. 익명 서비스라도 세션 토큰 검증은 필요하다.
2. WebSocket handshake 시 토큰 유효성 검사를 수행해야 한다.
3. XSS 방지를 위해 메시지 렌더링 시 HTML escape 처리가 필요하다.
4. IP는 표시용 일부 정보만 사용하고 원본 저장 범위는 최소화한다.
5. 관리자 API는 추후 별도 인증 체계로 보호해야 한다.

---

## 12. 단계별 구현 계획

### 1단계: MVP

1. 익명 세션 발급 API
2. 방 목록 조회 API
3. 메시지 저장 및 조회 API
4. WebSocket 실시간 채팅
5. 단일 채팅방 기준 동작 검증

### 2단계: 기본 서비스화

1. 다중 채팅방 지원
2. 커서 기반 무한 스크롤
3. 관리자 방 활성화 관리
4. 메시지 길이 제한과 간단한 도배 방지

### 3단계: 확장

1. Redis Pub/Sub 기반 확장
2. 로그인 사용자 지원
3. 1:1 대화 기능
4. 신고, 차단, 관리자 모니터링 기능

---

## 13. 현재 프로젝트 기준 다음 작업

현재 `build.gradle`에는 기본 Spring Boot 의존성만 포함되어 있습니다.
실제 구현을 시작하려면 아래 의존성을 우선 추가하는 것이 적절합니다.

1. `spring-boot-starter-web`
2. `spring-boot-starter-websocket`
3. `spring-boot-starter-data-jpa`
4. `spring-boot-starter-validation`
5. `spring-boot-starter-data-mongodb`
6. DB 드라이버

이후 우선순위는 다음이 적절합니다.

1. 방 조회 REST API
2. 익명 세션 발급 API
3. MongoDB 메시지 모델 및 조회 API
4. WebSocket 채팅 송수신

---

## 14. 결론

`t4lk`는 작은 규모에서 시작하지만, 구조적으로는 운영 데이터와 메시지 데이터를 분리해 확장성을 확보하는 방향이 적절합니다.
초기에는 단일 Spring Boot 인스턴스와 단순한 STOMP 브로커로 충분하며, 서비스 검증 이후 Redis와 인증 체계를 추가하는 방식이 현실적입니다.
