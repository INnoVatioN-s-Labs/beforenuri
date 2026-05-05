# Swagger 테스트 설명서

## 1. 이 문서의 목적

이 문서는 최근 추가한 Swagger 설정과 테스트용 API가 무엇을 하는지 이해하기 쉽게 설명하기 위한 문서입니다.

설명 대상은 아래 3가지입니다.

1. Swagger를 왜 추가했는가
2. `GET /health`와 `GET /api/rooms`는 각각 무엇을 확인하는가
3. 관련 Java 파일이 어떤 역할을 하는가

---

## 2. 이번에 추가한 파일과 변경 파일

### 변경한 파일

- [build.gradle](/Users/koyoungseok/Desktop/t4lk/t4lk/build.gradle:1)
- [HealthController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/HealthController.java:1)
- [RoomController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomController.java:1)
- [RoomResponse.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomResponse.java:1)

### 추가한 파일

- 없음

---

## 3. `build.gradle`에서 추가한 것

추가한 의존성은 아래입니다.

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3'
```

### 이게 하는 일

이 의존성은 Spring Boot 프로젝트에 Swagger UI를 붙여 줍니다.

이걸 추가하면 다음이 가능해집니다.

1. 브라우저에서 API 목록을 볼 수 있다
2. 각 API를 직접 눌러서 실행할 수 있다
3. 요청과 응답 JSON을 화면에서 바로 확인할 수 있다

### 왜 추가했는가

현재 단계에서는 프론트엔드 화면이 아직 없기 때문에, API가 동작하는지 빠르게 확인할 수 있는 도구가 필요했습니다.
Swagger UI는 그 역할을 가장 단순하게 해 줍니다.

---

## 4. `HealthController`를 추가한 이유

파일:
- [HealthController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/HealthController.java:1)

이 파일에는 아래 API가 들어 있습니다.

```text
GET /health
```

### 이 API의 역할

이 API는 서버가 살아 있는지 확인하는 가장 단순한 테스트용 API입니다.

정상 응답:

```json
{"status":"ok"}
```

### 이걸로 확인할 수 있는 것

1. 스프링 서버가 정상 기동했는지
2. `8080` 포트로 요청을 받고 있는지
3. JSON 응답이 정상적으로 반환되는지
4. Swagger에서 API 호출이 가능한지

### 이 API로는 확인할 수 없는 것

1. 채팅 기능
2. DB 연결
3. 방 목록 조회 로직
4. WebSocket 동작

즉 `GET /health`는 "서비스 기능 테스트"가 아니라 "서버 기본 동작 확인"입니다.

---

## 5. `RoomController`를 추가한 이유

파일:
- [RoomController.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomController.java:1)

이 파일에는 아래 API가 들어 있습니다.

```text
GET /api/rooms
```

### 이 API의 역할

이 API는 채팅방 목록을 조회하는 형태를 미리 보여 주기 위한 테스트용 기능 API입니다.

현재는 DB를 붙이지 않았기 때문에, 코드 안에서 임시 데이터 3개를 반환합니다.

### 왜 추가했는가

`GET /health`만 있으면 서버 생존 여부만 확인할 수 있습니다.
하지만 사용자가 보고 싶은 건 실제 서비스처럼 보이는 API 동작입니다.

그래서 `GET /api/rooms`를 추가해 아래를 확인할 수 있게 했습니다.

1. 실제 경로 형태의 REST API가 동작하는지
2. 목록(JSON 배열) 응답이 정상인지
3. Swagger에서 서비스 API 테스트가 가능한지

### 현재 응답 구조

응답은 대략 이런 형태입니다.

```json
[
  {
    "id": 1,
    "title": "자유 대화실",
    "description": "누구나 편하게 이야기하는 기본 방",
    "active": true
  }
]
```

### 중요한 점

지금은 진짜 DB 조회가 아닙니다.
즉 "방 목록 기능의 최종 구현"이 아니라 "방 목록 API의 형태를 먼저 확인하는 단계"입니다.

---

## 6. `RoomResponse`를 추가한 이유

파일:
- [RoomResponse.java](/Users/koyoungseok/Desktop/t4lk/t4lk/src/main/java/com/toyproject/t4lk/room/RoomResponse.java:1)

이 파일은 응답 전용 데이터 구조입니다.

```java
public record RoomResponse(
        Long id,
        String title,
        String description,
        boolean active
) {
}
```

### 이 파일의 역할

`GET /api/rooms`가 반환할 JSON 형태를 코드로 명확하게 정의합니다.

즉 이 클래스는 다음 의미를 가집니다.

1. 방 목록 응답에는 어떤 필드가 들어가는가
2. Swagger에 어떤 응답 모델이 보이는가
3. 나중에 서비스/DB 계층이 붙어도 컨트롤러 응답 형식을 유지할 수 있는가

### 왜 필요한가

응답 구조를 `Map`으로 대충 만들 수도 있지만, 그렇게 하면 필드 구조가 흐려집니다.
`RoomResponse`처럼 타입을 분리하면 API 구조가 명확해집니다.

---

## 7. Swagger에서 어떻게 테스트하는가

### 서버 실행

```bash
./gradlew bootRun
```

### Swagger 접속

```text
http://localhost:8080/swagger-ui.html
```

### 확인 가능한 API

1. `GET /health`
2. `GET /api/rooms`

### 테스트 방법

1. Swagger 화면에서 원하는 API를 펼친다
2. `Try it out` 버튼을 누른다
3. `Execute`를 누른다
4. 응답 본문과 상태 코드를 확인한다

---

## 8. 이번에 추가한 Swagger 어노테이션

이번에는 Swagger 화면에서 각 API의 목적이 바로 보이도록 설명용 어노테이션도 추가했습니다.

### `@Tag`

컨트롤러를 기능별로 묶어서 보여 줍니다.

- `HealthController`는 `System`
- `RoomController`는 `Rooms`

즉 Swagger 화면에서 API가 그룹별로 정리됩니다.

### `@Operation`

각 API가 무엇을 하는지 제목과 설명을 붙입니다.

예:

- `서버 상태 확인`
- `채팅방 목록 조회`

이 어노테이션 덕분에 Swagger에서 단순히 경로만 보는 게 아니라, 이 API가 왜 존재하는지도 같이 이해할 수 있습니다.

### `@ApiResponse`

성공 시 어떤 응답이 오는지 설명합니다.

예:

- `200`: 서버가 정상 동작 중입니다
- `200`: 채팅방 목록 조회에 성공했습니다

이 어노테이션은 테스트할 때 "이 응답이 정상인지" 판단하는 기준이 됩니다.

### `@Schema`

응답 모델의 필드 하나하나에 의미를 붙입니다.

예:

- `id`: 채팅방 번호
- `title`: 채팅방 제목
- `description`: 채팅방 설명
- `active`: 채팅방 활성 여부

이 어노테이션 덕분에 Swagger 모델 화면에서 각 필드가 무엇인지 이해하기 쉬워집니다.

### 왜 필요한가

어노테이션이 없으면 Swagger는 경로와 타입만 기계적으로 보여 줍니다.
어노테이션이 있으면 "이 API가 무슨 테스트용인지", "응답 필드가 무슨 뜻인지"를 화면에서 바로 읽을 수 있습니다.

---

## 9. 각각의 API를 통해 무엇을 이해하면 되는가

### `GET /health`

이 API는 "서버가 살아 있나?"를 확인하는 용도입니다.

### `GET /api/rooms`

이 API는 "서비스 기능처럼 생긴 API가 실제로 잘 응답하나?"를 확인하는 용도입니다.

즉 둘의 차이는 아래와 같습니다.

- `GET /health`: 서버 생존 확인
- `GET /api/rooms`: 기능 형태 확인

---

## 10. 아직 안 된 것

현재 상태에서 아직 구현되지 않은 것은 아래와 같습니다.

1. 실제 DB에서 방 목록 조회
2. 방 상세 조회
3. 익명 세션 발급
4. 메시지 조회
5. WebSocket 채팅

즉 지금은 "Swagger로 테스트 가능한 최소 구조"까지 만든 상태입니다.

---

## 11. 다음 단계

다음으로 이어서 만들기 좋은 API는 아래입니다.

1. `GET /api/rooms/{roomId}`
2. `POST /api/session/anonymous`
3. `GET /api/rooms/{roomId}/messages`

이 순서로 가면 Swagger 안에서 점점 실제 서비스처럼 테스트할 수 있게 됩니다.
