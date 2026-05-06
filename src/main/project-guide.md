# `t4lk` 통합 변경 및 구현 설명서

## 1. 이 문서의 목적

이 문서는 지금까지 프로젝트에 어떤 변화가 있었는지 한 파일로 정리한 통합 문서입니다.

이 문서에서 설명하는 내용은 아래와 같습니다.

1. 서비스가 어떤 방향으로 설계되었는가
2. 초기 서버/Swagger 테스트 구조를 왜 만들었는가
3. 인메모리 데이터를 왜 JPA 엔티티로 바꿨는가
4. MySQL과 테스트 구조는 어떻게 연결되었는가
5. 지금 기능을 어떻게 확인하면 되는가

---

## 2. 프로젝트의 현재 방향

이 프로젝트는 레트로 PC통신 감성의 익명 채팅 서비스를 목표로 합니다.

현재 기준 핵심 흐름은 아래와 같습니다.

1. 사용자가 서버에 접속한다
2. 방 목록을 조회한다
3. 특정 방을 선택한다
4. 익명 세션을 발급받는다
5. 방 메시지 목록을 조회한다
6. 이후 WebSocket 기반 실시간 채팅으로 확장한다

즉 지금은 완성형 채팅 서비스가 아니라, 그 흐름을 실제 코드와 API로 검증할 수 있는 단계까지 올라온 상태입니다.

---

## 3. 처음에 했던 작업

### 3.1. 웹 서버로 동작하도록 수정

처음 프로젝트는 기본 Spring Boot 상태였고, 웹 의존성이 없어서 실행하면 바로 종료되는 구조였습니다.

그래서 `spring-boot-starter-web`을 추가했습니다.

### 왜 필요한가

이 의존성이 있어야 스프링이 Tomcat 기반 웹 서버로 기동하고, `http://localhost:8080`에서 요청을 받을 수 있습니다.

즉 "실행하면 바로 꺼지는 상태"를 "웹 서버로 계속 살아 있는 상태"로 바꾼 것입니다.

---

## 4. Swagger를 추가한 이유

### 무엇을 추가했는가

`build.gradle`에 아래 의존성을 추가했습니다.

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3'
```

### 왜 필요한가

프론트엔드 화면이 없는 상태에서도 API를 바로 확인할 수 있어야 했기 때문입니다.

Swagger UI를 추가하면 브라우저에서:

1. 어떤 API가 있는지 본다
2. 각 API가 무슨 역할인지 읽는다
3. 직접 버튼으로 실행한다
4. 응답 JSON과 상태 코드를 확인한다

즉 Swagger는 “기능이 되는지 안 되는지 빠르게 확인하는 테스트 화면” 역할을 합니다.

### 접속 주소

```text
http://localhost:8080/swagger-ui.html
```

---

## 5. 처음 만든 테스트용 기능 API

Swagger만 붙이면 비어 있기 때문에, 실제로 눌러볼 API를 먼저 만들었습니다.

### `GET /health`

역할:
- 서버가 살아 있는지 확인

이 API는 아래를 확인합니다.

1. 스프링 서버가 기동했는지
2. HTTP 요청을 받을 수 있는지
3. JSON 응답이 정상 반환되는지

정상 응답:

```json
{
  "status": "ok"
}
```

### `GET /api/rooms`

역할:
- 채팅방 목록을 조회하는 API 형태를 먼저 검증

처음에는 이 API가 인메모리 샘플 데이터를 반환하도록 만들었습니다.

### 왜 만들었는가

`health`만 있으면 서버 생존만 확인할 수 있습니다.
하지만 실제 서비스 같은 API도 눌러봐야 프로젝트가 어디까지 구현됐는지 이해할 수 있습니다.

그래서 방 목록 API를 추가했습니다.

---

## 6. Swagger에 설명 어노테이션을 넣은 이유

각 API가 무슨 기능인지 바로 이해되도록 아래 어노테이션을 붙였습니다.

### `@Tag`

API를 그룹별로 묶습니다.

예:
- `System`
- `Rooms`
- `Session`
- `Messages`

### `@Operation`

이 API가 무엇을 하는지 제목과 설명을 붙입니다.

예:
- 서버 상태 확인
- 채팅방 목록 조회
- 익명 세션 발급

### `@ApiResponse`

정상 응답 또는 실패 응답이 무엇인지 Swagger에 보여줍니다.

### `@Schema`

응답 JSON 필드가 무엇을 의미하는지 설명합니다.

### 왜 중요한가

경로만 보면 기능을 추측해야 합니다.
설명 어노테이션이 있으면 Swagger 안에서 바로 “왜 이 API가 있는지” 이해할 수 있습니다.

---

## 7. 인메모리 기반에서 JPA 기반으로 바꾼 이유

처음 서비스 로직은 인메모리 구조였습니다.

예:
- `RoomService`는 `List`에서 방을 꺼냈고
- `ChatService`는 `Map<Long, List<...>>`에서 메시지를 꺼냈고
- `SessionService`는 메모리에서 토큰을 만들기만 했습니다

이 방식은 테스트용으로는 빠르지만, 실제 서비스 데이터는 남지 않습니다.

그래서 다음 단계에서 전부 JPA 엔티티 기반으로 마이그레이션했습니다.

### 왜 바꿨는가

1. 실제 DB에 데이터가 저장되어야 함
2. 서비스가 재시작돼도 데이터 흐름이 유지되어야 함
3. 이후 기능 확장 시 JPA 관계 매핑이 필요함
4. 테스트도 “실제 저장소를 사용하는 구조” 기준으로 검증해야 함

---

## 8. MySQL과 JPA를 추가한 이유

### 추가한 의존성

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.mysql:mysql-connector-j'
testRuntimeOnly 'com.h2database:h2'
```

### 각각의 역할

- `spring-boot-starter-data-jpa`
  엔티티, 리포지토리, Hibernate ORM 기능 제공

- `mysql-connector-j`
  `beforenuri_db`와 실제 연결

- `h2`
  테스트를 MySQL 없이 돌리기 위한 메모리 DB

즉 운영 확인은 MySQL, 자동 테스트는 H2로 분리한 구조입니다.

---

## 9. MySQL 연결 설정

현재 메인 설정은 아래 방향으로 되어 있습니다.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/beforenuri_db...
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
```

### 왜 이렇게 했는가

1. DB 이름은 요청대로 `beforenuri_db` 고정
2. 아이디/비밀번호는 환경마다 다르니 환경변수로 분리
3. 엔티티를 기준으로 테이블이 자동 반영되게 `ddl-auto=update` 사용

즉 비밀번호가 있는 환경에서도 코드 수정 없이 환경변수만 주면 됩니다.

실행 예시:

```bash
export DB_USERNAME=본인아이디
export DB_PASSWORD=본인비밀번호
./gradlew bootRun
```

---

## 10. `BaseEntity`를 만든 이유

파일:
- [BaseEntity.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/common/BaseEntity.java:1)

모든 엔티티는 공통적으로 아래 필드를 가져야 한다는 요구사항이 있었습니다.

1. `createdAt`
2. `updatedAt`
3. `isDeleted`

그래서 엔티티마다 반복하지 않고 공통 부모 클래스로 분리했습니다.

### 이 클래스가 하는 일

- 처음 저장될 때 `createdAt`, `updatedAt`, `isDeleted=false` 자동 설정
- 수정될 때 `updatedAt` 자동 갱신

### 왜 필요한가

1. 엔티티마다 중복 코드 제거
2. 생성/수정 시각을 자동으로 관리
3. soft delete 구조를 기본 탑재

---

## 11. 현재 JPA 엔티티 구조

### `Room`

파일:
- [Room.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/Room.java:1)

역할:
- 채팅방 정보 저장

주요 값:
- `id`
- `title`
- `description`
- `active`

### `AnonymousSession`

파일:
- [AnonymousSession.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/session/AnonymousSession.java:1)

역할:
- 익명 세션 토큰과 표시용 닉네임 저장

주요 값:
- `sessionToken`
- `displayName`

### `ChatMessage`

파일:
- [ChatMessage.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatMessage.java:1)

역할:
- 채팅 메시지 저장

주요 값:
- `room`
- `senderName`
- `messageType`
- `content`

### `ChatMessageType`

파일:
- [ChatMessageType.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatMessageType.java:1)

역할:
- `CHAT`, `SYSTEM` 같은 메시지 유형을 enum으로 관리

---

## 12. 리포지토리를 추가한 이유

추가한 리포지토리:

- [RoomRepository.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomRepository.java:1)
- [AnonymousSessionRepository.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/session/AnonymousSessionRepository.java:1)
- [ChatMessageRepository.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatMessageRepository.java:1)

### 역할

컨트롤러나 서비스가 직접 SQL을 쓰지 않고, JPA 리포지토리를 통해 엔티티를 조회/저장하도록 바꿨습니다.

### 중요한 점

조회 메서드 이름에 `isDeleted=false` 조건을 반영했습니다.

예:
- `findAllByIsDeletedFalseOrderByIdAsc()`
- `findByIdAndIsDeletedFalse(...)`
- `findAllByRoom_IdAndIsDeletedFalseOrderByCreatedAtAsc(...)`

즉 soft delete 설계를 조회 계층에도 반영한 것입니다.

---

## 13. 서비스 로직이 어떻게 바뀌었는가

### `RoomService`

기존:
- 메모리 `List`에서 방 조회

현재:
- `RoomRepository`에서 방 목록/상세 조회

### `SessionService`

기존:
- 토큰/닉네임을 만들기만 하고 저장하지 않음

현재:
- 토큰/닉네임 생성 후 `AnonymousSessionRepository`에 저장
- 저장된 세션 엔티티를 응답으로 반환

### `ChatService`

기존:
- 메모리 `Map`에서 메시지 조회

현재:
- `ChatMessageRepository`에서 방별 메시지 조회
- `RoomService`로 방 존재 여부 검증

즉 지금은 API가 모두 DB 기반 데이터 흐름으로 동작합니다.

---

## 14. 샘플 데이터를 자동으로 넣는 이유

파일:
- [DataInitializer.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/common/DataInitializer.java:1)

### 역할

서버 실행 시 아래 샘플 데이터를 자동으로 넣습니다.

1. 방 3개
2. 메시지 몇 개

### 왜 필요한가

DB만 연결하고 데이터가 비어 있으면 Swagger에서 눌러볼 것이 없습니다.
그래서 실행 직후에도 바로 테스트 가능하도록 샘플 데이터를 넣었습니다.

### 주의

테스트 환경에서는 꺼 두었습니다.

이유는 테스트가 샘플 데이터에 의존하면 안 되기 때문입니다.

---

## 15. 지금 Swagger에서 볼 수 있는 기능

현재 Swagger에서 테스트 가능한 주요 API는 아래와 같습니다.

### `GET /health`

역할:
- 서버 생존 확인

### `GET /api/rooms`

역할:
- 채팅방 목록 조회

### `GET /api/rooms/{roomId}`

역할:
- 채팅방 상세 조회

### `POST /api/session/anonymous`

역할:
- 익명 세션 발급 및 저장

### `GET /api/rooms/{roomId}/messages`

역할:
- 특정 방의 메시지 목록 조회

---

## 16. 기능 확인은 어떻게 해야 하는가

기능 확인은 MySQL만 보면 되는 것이 아닙니다.

### MySQL에서 확인되는 것

1. 테이블이 생성됐는지
2. 데이터가 저장됐는지
3. `createdAt`, `updatedAt`, `isDeleted`가 잘 들어갔는지

### MySQL에서 확인되지 않는 것

1. URL 라우팅이 맞는지
2. API 응답 코드가 맞는지
3. JSON 응답 구조가 맞는지
4. 없는 방 조회 시 `404`가 나는지

즉 확인은 두 단계로 해야 합니다.

1. Swagger에서 API를 호출해 기능이 동작하는지 본다
2. MySQL에서 결과가 저장됐는지 본다

---

## 17. 실제 확인 방법

### 1. 서버 실행

```bash
export DB_USERNAME=본인아이디
export DB_PASSWORD=본인비밀번호
./gradlew bootRun
```

### 2. Swagger 접속

```text
http://localhost:8080/swagger-ui.html
```

### 3. 추천 확인 순서

1. `GET /health`
2. `GET /api/rooms`
3. `GET /api/rooms/{roomId}`
4. `POST /api/session/anonymous`
5. `GET /api/rooms/{roomId}/messages`

### 4. MySQL에서 확인

```sql
USE beforenuri_db;

SHOW TABLES;

SELECT * FROM rooms;
SELECT * FROM chat_messages;
SELECT * FROM anonymous_sessions;
```

### 예시 확인 흐름

1. Swagger에서 `POST /api/session/anonymous` 실행
2. 응답 JSON 확인
3. MySQL에서 `SELECT * FROM anonymous_sessions;` 확인

이렇게 해야 API 응답과 DB 저장 둘 다 확인됩니다.

---

## 18. 테스트를 어떻게 구성했는가

추가한 테스트:

- [T4lkApplicationTests.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/test/java/com/toyproject/t4lk/T4lkApplicationTests.java:1)
- [RoomServiceTest.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/test/java/com/toyproject/t4lk/room/RoomServiceTest.java:1)
- [ChatServiceTest.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/test/java/com/toyproject/t4lk/chat/ChatServiceTest.java:1)
- [SessionServiceTest.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/test/java/com/toyproject/t4lk/session/SessionServiceTest.java:1)

### 각 테스트 역할

`T4lkApplicationTests`
- 스프링 컨텍스트 기동
- `health` 응답 확인

`RoomServiceTest`
- 방 목록 조회
- 방 상세 조회
- 없는 방 예외 처리
- `BaseEntity` 공통 필드 확인

`ChatServiceTest`
- 방별 메시지 조회
- 없는 방 예외 처리

`SessionServiceTest`
- 익명 세션 발급 및 저장
- 세션 엔티티의 `BaseEntity` 필드 확인

### 실행 명령

```bash
./gradlew test
```

### 왜 H2를 쓰는가

테스트는 MySQL 상태에 의존하면 안 되기 때문입니다.
그래서 테스트 전용 설정에서는 H2 메모리 DB를 사용합니다.

---

## 19. 지금까지 바뀐 내용을 한 줄씩 요약하면

1. 서버가 웹 애플리케이션으로 동작하도록 바뀜
2. Swagger로 API를 직접 테스트할 수 있게 됨
3. 초기 기능 API가 추가됨
4. Swagger 설명 어노테이션으로 각 기능 의미가 보이게 됨
5. 인메모리 데이터 구조가 JPA 엔티티 구조로 전환됨
6. MySQL `beforenuri_db`와 연결되도록 설정됨
7. 모든 엔티티가 `BaseEntity`를 상속받게 됨
8. 기능별 JUnit 테스트가 추가됨

---

## 20. 다음 단계

지금 다음으로 가장 자연스럽게 이어갈 수 있는 기능은 아래입니다.

1. `POST /api/rooms/{roomId}/messages` 메시지 작성 API
2. soft delete 실제 활용
3. WebSocket/STOMP 채팅 연결
4. 컨트롤러 레벨 테스트 보강

현재 상태는 “DB가 연결된 읽기 중심 초기 골격”까지 구현된 상태로 보면 됩니다.
