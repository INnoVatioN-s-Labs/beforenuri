# 리뷰 대응 정리

## 목적

이 문서는 이번 PR에서 받은 리뷰 피드백 중 어떤 항목을 반영했는지, 무엇이 아직 남아 있는지 정리하기 위한 문서입니다.

---

## 이번에 반영한 항목

### 1. MongoDB Atlas 자격증명 평문 제거

- `application.properties`에 직접 적혀 있던 MongoDB Atlas URI를 제거했습니다.
- 현재는 아래처럼 환경변수 기반으로 동작합니다.

```properties
spring.mongodb.uri=${MONGODB_URI}
```

### 2. MySQL 비밀번호 fallback 제거

- `DB_PASSWORD:1234` 형태의 약한 fallback을 제거했습니다.
- 현재는 아래처럼 환경변수 기반으로 동작합니다.

```properties
spring.datasource.password=${DB_PASSWORD:}
```

### 3. WebSocket / CORS origin 와일드카드 제거

- `WebSocketConfig`, `WebConfig`에서 `*` 허용을 제거했습니다.
- 현재는 `app.allowed-origins` 설정값만 허용합니다.

```properties
app.allowed-origins=http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173,http://127.0.0.1:5173
```

### 4. Mongo 저장 로직에서 `@Transactional` 제거

- `ChatService`는 MongoDB 저장만 담당하므로 JPA 트랜잭션 어노테이션을 제거했습니다.
- Room 쪽 JPA 서비스는 기존처럼 `@Transactional`을 유지합니다.

### 5. Room 존재 검증 방식 개선

- `ChatService`에서 `getRoomEntity()`를 반환값 없이 검증용으로 호출하던 패턴을 정리했습니다.
- `RoomService.validateRoomExists()`와 `RoomRepository.existsByIdAndIsDeletedFalse()`를 추가했습니다.

### 6. MongoDB 인덱스 추가

- `ChatMessage`에 아래 복합 인덱스를 추가했습니다.

```java
@CompoundIndex(name = "room_active_created_idx", def = "{'roomId': 1, 'deleted': 1, 'createdAt': 1}")
```

### 7. 시간대 보정

- `ChatMessage`의 `createdAt`, `updatedAt` 생성/수정 시 `Asia/Seoul` 기준을 사용하도록 변경했습니다.

### 8. `Objects::nonNull`로 정리

- `DataInitializer.findLegacyRoom()`에서 `filter(room -> room != null)`를 `Objects::nonNull`로 정리했습니다.

### 9. Flyway 도입

- `ddl-auto=update`에만 의존하지 않도록 Flyway를 추가했습니다.
- 운영/로컬 MySQL 기준 마이그레이션 파일을 추가했습니다.

추가된 파일:

- `src/main/resources/db/migration/V1__create_core_tables.sql`
- `src/main/resources/db/migration/V2__backfill_legacy_rooms.sql`

### 10. 테스트 재정비

- 테스트 환경은 Flyway와 분리해서 유지했습니다.
- 테스트 프로파일에서는 H2 + `create-drop` 구조를 사용합니다.
- 전체 테스트(`./gradlew test`) 통과를 다시 확인했습니다.

---

## 아직 남아 있는 항목

### 1. MongoDB Atlas 비밀번호 회전

- 코드에서는 제거했지만, 한 번 PR에 노출된 값은 더 이상 안전하지 않습니다.
- Atlas 콘솔에서 해당 사용자 비밀번호를 변경하거나 사용자를 재생성해야 합니다.

### 2. 레거시 MySQL `chat_messages` 처리 방안

- 채팅 메시지 저장소는 MongoDB로 전환했습니다.
- 기존 MySQL `chat_messages` 데이터를 폐기할지, 이관할지 정책 결정이 아직 필요합니다.

### 3. REST POST 메시지와 WebSocket broadcast 정책

- 현재 REST `POST /api/rooms/{roomId}/messages`는 저장만 하고 broadcast는 하지 않습니다.
- 이 API를 관리자/시드 전용으로 볼지, 일반 채팅 입력과 동일한 경로로 볼지 정책 정리가 필요합니다.

### 4. `messageId` 타입 변경 공지

- MongoDB 전환으로 `ChatMessageResponse.id`는 `Long`에서 `String`으로 변경되었습니다.
- 프론트와의 계약 변경 사항이므로 PR 설명에 breaking change로 명시할 필요가 있습니다.

### 5. `ChatSocketMessageResponse.type` 유지 여부

- 현재는 `messageType`과 사실상 같은 의미로 전달됩니다.
- 향후 JOIN/LEAVE 같은 시스템 이벤트를 확장할 계획이 없다면 축소를 검토할 수 있습니다.

---

## 실행 메모

로컬 실행 시 환경변수는 코드에 두지 않고 `.env` 또는 IntelliJ Run Configuration 환경변수로 주입합니다.

예시:

```text
DB_USERNAME=root
DB_PASSWORD=1234
MONGODB_URI=mongodb+srv://...
```

---

## 현재 상태 요약

- 보안상 즉시 위험한 항목은 우선 반영했습니다.
- 구조상 혼동을 줄이는 리팩터링도 함께 적용했습니다.
- DB 스키마 변경은 Flyway로 관리하도록 전환했습니다.
- 남은 항목은 정책 결정 또는 후속 PR 범위로 분리 가능한 상태입니다.
