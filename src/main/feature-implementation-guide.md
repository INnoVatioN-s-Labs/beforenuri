# 기능 구현 설명서

## 1. 이 문서의 목적

이 문서는 이번에 새로 추가한 기능들이 무엇인지, 왜 이 순서로 만들었는지, 각 파일이 어떤 역할을 하는지 설명하기 위한 문서입니다.

이번에 추가한 목표는 "Swagger에서 눌러보면 서비스 흐름이 보이도록 만드는 것"입니다.

---

## 2. 이번에 추가한 기능 목록

이번에 추가한 기능은 아래 4개입니다.

1. `GET /api/rooms/{roomId}`: 채팅방 상세 조회
2. `POST /api/session/anonymous`: 익명 세션 발급
3. `GET /api/rooms/{roomId}/messages`: 메시지 목록 조회
4. 존재하지 않는 방 조회 시 `404` 공통 에러 응답

이 4개를 고른 이유는 채팅 서비스의 기본 흐름을 가장 단순하게 보여 주기 때문입니다.

1. 방 목록을 본다
2. 방 하나를 선택한다
3. 익명 이름을 발급받는다
4. 메시지를 불러온다

즉 지금은 WebSocket 실시간 채팅 전 단계까지의 흐름을 테스트할 수 있게 만든 상태입니다.

---

## 3. 왜 컨트롤러만 늘리지 않고 서비스도 만들었는가

기존에는 `RoomController` 안에 임시 데이터가 직접 들어 있었습니다.
기능이 늘어나면 컨트롤러 안에 하드코딩이 계속 쌓여 구조가 금방 지저분해집니다.

그래서 이번에는 다음처럼 역할을 나눴습니다.

- Controller: URL 요청을 받는 역할
- Service: 실제 데이터 조립과 조회 역할
- Response DTO: 어떤 JSON을 반환할지 정의하는 역할

이렇게 해 둬야 나중에 DB를 붙일 때도 컨트롤러를 크게 뜯어고치지 않아도 됩니다.

---

## 4. `GET /api/rooms/{roomId}`를 추가한 이유

관련 파일:

- [RoomController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomController.java:1)
- [RoomService.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomService.java:1)
- [RoomNotFoundException.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomNotFoundException.java:1)

### 이 기능이 하는 일

사용자가 특정 방 번호를 눌렀을 때 그 방의 상세 정보를 반환합니다.

예:

```text
GET /api/rooms/1
```

### 왜 필요한가

`GET /api/rooms`는 목록만 줍니다.
실제 서비스는 목록을 본 뒤 특정 방 하나를 선택하는 단계가 필요합니다.

그래서 상세 조회 API를 추가했습니다.

### 테스트할 때 무엇을 보면 되는가

`GET /api/rooms/1`
- 정상 응답이면 상세 조회 흐름이 동작하는 것

`GET /api/rooms/999`
- `404`가 나오면 없는 방 예외 처리도 정상이라는 뜻

---

## 5. `POST /api/session/anonymous`를 추가한 이유

관련 파일:

- [SessionController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/session/SessionController.java:1)
- [SessionService.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/session/SessionService.java:1)
- [AnonymousSessionResponse.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/session/AnonymousSessionResponse.java:1)

### 이 기능이 하는 일

익명 사용자가 채팅에 들어가기 전에 임시 토큰과 표시용 닉네임을 발급받습니다.

응답 예시:

```json
{
  "sessionToken": "anon-token-1-honorable-panty",
  "displayName": "명예로운 팬티_192.168"
}
```

### 왜 필요한가

이 서비스의 핵심 특징 중 하나가 익명 입장입니다.
즉 채팅 기능을 만들기 전에 "익명 사용자 식별" 기능이 먼저 있어야 흐름이 자연스럽습니다.

### 지금 단계에서의 의미

아직 진짜 인증 시스템은 아닙니다.
지금은 Swagger에서 눌러보며 "익명 입장 API가 이런 형태로 생길 것"을 확인하는 단계입니다.

---

## 6. `GET /api/rooms/{roomId}/messages`를 추가한 이유

관련 파일:

- [ChatController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatController.java:1)
- [ChatService.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatService.java:1)
- [ChatMessageResponse.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/chat/ChatMessageResponse.java:1)

### 이 기능이 하는 일

선택한 채팅방의 최근 메시지 목록을 반환합니다.

예:

```text
GET /api/rooms/1/messages
```

### 왜 필요한가

채팅 서비스는 방에 들어가면 최근 메시지를 먼저 보여줘야 합니다.
WebSocket 실시간 송수신과 별개로, 과거 내역 조회 API는 따로 필요합니다.

그래서 메시지 조회 기능을 먼저 만든 것입니다.

### 지금 단계에서의 의미

현재는 DB 대신 임시 데이터가 들어 있습니다.
즉 목적은 "메시지 조회 API의 모양과 테스트 흐름을 먼저 잡는 것"입니다.

---

## 7. `404` 공통 에러 응답을 추가한 이유

관련 파일:

- [GlobalExceptionHandler.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/common/GlobalExceptionHandler.java:1)
- [ErrorResponse.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/common/ErrorResponse.java:1)

### 이 기능이 하는 일

없는 방을 조회하면 아래 같은 JSON을 반환합니다.

```json
{
  "code": "ROOM_NOT_FOUND",
  "message": "존재하지 않는 채팅방입니다. roomId=999"
}
```

### 왜 필요한가

정상 응답만 있으면 테스트가 반쪽입니다.
실제 서비스는 실패할 때도 어떤 형식으로 응답할지 정해져 있어야 합니다.

그래서 이번에는 최소한의 공통 에러 응답 구조도 같이 만들었습니다.

---

## 8. Swagger에서 지금 테스트할 수 있는 흐름

서버 실행:

```bash
./gradlew bootRun
```

Swagger 접속:

```text
http://localhost:8080/swagger-ui.html
```

### 추천 테스트 순서

1. `GET /health`
2. `GET /api/rooms`
3. `GET /api/rooms/1`
4. `POST /api/session/anonymous`
5. `GET /api/rooms/1/messages`
6. `GET /api/rooms/999`

### 이 순서가 좋은 이유

이 순서는 서버 생존 확인부터 방 선택, 익명 입장, 메시지 조회, 예외 처리까지 한 번에 따라갈 수 있게 해 줍니다.

---

## 9. 자동 테스트로 추가한 것

관련 파일:

- [T4lkApplicationTests.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/test/java/com/toyproject/t4lk/T4lkApplicationTests.java:1)

이번에는 `MockMvc` 기반 테스트도 추가했습니다.

확인하는 내용:

1. `/health`가 `200`인지
2. `/api/rooms`가 목록을 반환하는지
3. 없는 방 조회 시 `404`인지
4. 익명 세션 발급 응답에 필드가 들어 있는지

즉 Swagger에서 수동 테스트하는 것과 별도로, 코드 수준에서도 기본 동작을 검증하게 했습니다.

---

## 10. 지금은 아직 임시 데이터 기반인 이유

현재 프로젝트는 DB, JPA, MongoDB, WebSocket이 아직 붙지 않았습니다.
그 상태에서 곧바로 복잡한 구현으로 들어가면 테스트 포인트가 너무 많아집니다.

그래서 이번 단계의 목적은 아래 두 가지였습니다.

1. API 설계가 맞는지 먼저 확인
2. Swagger에서 사람이 직접 테스트 가능한 구조를 먼저 만들기

즉 지금은 "최종 기능 완성"이 아니라 "기능 흐름 검증용 골격 구축" 단계입니다.

---

## 11. 다음에 이어서 만들 기능

다음으로 붙이기 좋은 기능은 아래입니다.

1. `POST /api/rooms/{roomId}/messages`: 메시지 작성
2. `GET /api/rooms/{roomId}/messages`에 페이지네이션 파라미터 추가
3. WebSocket/STOMP 연결
4. 실제 DB 연동

지금 상태에서는 먼저 메시지 작성 API를 붙이는 게 가장 자연스럽습니다.
