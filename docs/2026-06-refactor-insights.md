# 2026-06 기능 추가 / 리팩토링 — 백엔드 인사이트 노트

> 작성 의도: 잠깐 자리를 비운 사이 진행된 백엔드·프론트 변경을, **"무엇을 / 왜 / 어떤 트레이드오프 / 결과"** 관점으로 정리한 학습용 기록.
> 백엔드 취업 준비 관점에서 면접에 바로 써먹을 수 있는 포인트(★)를 함께 표시했다.
> 코드와 같이 보면 좋다 — 각 항목에 관련 PR 번호를 달아뒀다.

---

## 0. 한눈에 보기

| PR | 주제 | 한 줄 |
|----|------|------|
| #12 | 대화실 presence | STOMP 구독/해제 이벤트로 입장·퇴장 알림 + 접속자 수 |
| #13 | 접속자 목록 | `/목록` 명령용 occupants 조회 API |
| #14 | 자유게시판 | PC통신식 답글 트리(parentId → DFS) |
| #15 | 회원 인증(JWT) **+ 첫 배포 실패** | 회원 도입 시도 → 운영 다운 |
| #16 | 배포 복구 | CD에 `docker system prune` 추가(디스크 풀 해결) |
| #17 | 회원 인증 재도입 **+ Flyway 수정** | 다운의 근본 원인(Spring Boot 4 Flyway) 해결 |
| #18 | 익명 닉네임 확장 | 4개 대각선 조합 → 24×24 독립 랜덤 |
| (프론트) | 컴포넌트 분리·디자인 토큰·회원 UI·CLI 입력 | — |

> 이 기간의 **핵심 스토리 = "회원 기능을 넣다가 운영을 한 번 죽이고, 원인을 추적해 되살린 과정"**(#15 → #16 → #17). 아래 3번이 가장 학습 가치가 크다.

---

## 1. 회원 인증 — JWT(access/refresh) 설계 [#15, #17]

**배경**: 기존엔 익명 세션(랜덤 닉 + IP)만 있었다. 회원(고정 닉) 기능을 추가하되, **비회원도 그대로 익명으로 쓸 수 있어야** 했다.

**결정**
- 비밀번호: **BCrypt**(salt를 해시에 자동 내장 — 별도 salt 컬럼 불필요)
- **accessToken**: JWT, 1시간, **stateless**(서명만 검증, DB 조회 없음). claim에 `memberId`/`displayName`
- **refreshToken**: 7일, **DB(`refresh_tokens`) 저장**, 갱신 시 **회전(rotation)** — 쓴 rtk는 폐기하고 새로 발급
- Redis가 없어 rtk는 RDB에 쌓고, **매일 02:00 만료분을 스케줄러로 정리**(`@Scheduled`)

**왜 이렇게?**
- atk를 stateless JWT로 두면 매 요청마다 DB를 안 친다(성능·단순함). 대신 **즉시 무효화가 어렵다**는 단점 → 그래서 수명을 짧게(1h).
- rtk를 DB에 두는 이유는 **로그아웃/탈취 시 폐기**가 가능해야 하기 때문(stateless면 폐기 불가). 회전까지 하면 **탈취된 rtk 재사용 탐지**에 유리.

**트레이드오프**
- Redis 대신 RDB+스케줄러 → 인프라 단순(현재 규모엔 충분), 단 rtk 조회가 DB 부하. 트래픽 커지면 Redis로.

★ **면접 포인트**: "왜 atk/rtk를 나눴나", "stateless의 장단점과 짧은 만료로 보완", "rtk 회전이 막는 공격(재사용)", "Redis 없이 만료 정리(스케줄러)" — 전부 단골 질문.

### 1-1. 인증 진입점 통합 — `IdentityResolver` [#17]
- 채팅(STOMP CONNECT)·게시판 작성 등 여러 진입점에서 **"이 토큰이 회원이냐 익명이냐"** 를 판단해야 했다.
- 한 곳(`IdentityResolver`)으로 모음: **JWT로 파싱되면 회원 닉, 아니면 기존 익명 세션 토큰으로 해석.**
- ★ 의미: 호출부(STOMP 인터셉터, 컨트롤러)는 신원 판단 로직을 모른 채 닉네임만 받는다 → **관심사 분리**. 발신자 이름은 클라이언트 입력이 아니라 **서버가 토큰으로 결정** → 사칭 방지.

---

## 2. RDB / MongoDB 이원화

**결정**: 변하지 않는 메타데이터는 RDB, 대량·추가 위주 데이터는 Mongo.
- **RDB(MySQL/JPA)**: `rooms`, `anonymous_sessions`, `members`, `refresh_tokens` — 정합성/유니크 제약/관계가 중요한 것
- **MongoDB**: `chat_messages`, `posts` — 계속 쌓이고 읽기 위주인 것

**왜?** 채팅 메시지는 수가 무한정 늘고 스키마가 단순해 document store가 적합. 방·회원은 건수가 적고 제약(unique username 등)이 필요해 RDB가 적합.

★ **면접 포인트**: "폴리글랏 퍼시스턴스를 왜/언제 쓰나" — 데이터 특성(증가율·정합성·조회 패턴)으로 설명할 수 있어야 함.

---

## 3. ★★ Flyway 도입과 운영 장애 복구 (이번의 하이라이트) [#15→#16→#17]

**상황**: 회원 기능엔 `members`/`refresh_tokens` 새 테이블이 필요. 그런데 운영은 `ddl-auto: validate`(스키마 검증만, 생성 안 함)이고 **운영 RDB에 직접 접속 권한이 없다.**

**1차 시도(#15)와 장애**
- `flyway-core` + `flyway-mysql` 의존성 + 마이그레이션 SQL 추가 후 배포 → **운영 다운(502)**
- 로그: `Schema validation: missing table [members]` → 즉 **Flyway가 테이블을 안 만들었다.** 로그에 Flyway 실행 흔적 자체가 없음.

**2차 사고(복구 중 또 실패)**
- 복구하려고 revert 후 재배포 → 이번엔 `no space left on device` → **EC2 디스크 풀**로 이미지 로드 실패.
- CD가 **기존 컨테이너를 먼저 지우고 새로 띄우는** 구조라, 새 컨테이너 기동 실패 = 곧 서비스 다운.
- 조치(#16): 배포 스크립트에 `docker rm` → `docker system prune -af`(미사용 이미지 정리) → `docker load` 순서로 디스크 확보 → 복구 성공.

**근본 원인(#17)**
- **Spring Boot 4.0은 `flyway-core` 단독으로 Flyway를 자동설정하지 않는다.** `application.yml`에 설정이 있어도 마이그레이션이 조용히 안 돈다. → **`spring-boot-starter-flyway`** 가 있어야 자동설정 활성화(Spring Boot 4 breaking change).
- `flyway-core` → `spring-boot-starter-flyway`로 교체 후 재배포 → Flyway가 V2 마이그레이션 실행 → `members` 생성 → 정상 기동.
- 기존 운영 테이블 보존을 위해 `baseline-on-migrate: true`(이미 있는 스키마는 baseline 처리, 이후 버전만 적용).

★ **면접 포인트 (스토리텔링용, 매우 강력)**:
- "운영 장애를 어떻게 진단했나" → 배포 로그에서 `missing table` → Flyway 미실행 → 의존성/버전 이슈로 좁혀감
- "롤백이 또 막힌 경험" → 디스크 풀, CD 구조상 다운 → prune으로 해결 (배포 파이프라인 이해)
- "프레임워크 메이저 업글의 함정" → Spring Boot 4 자동설정 변경
- 교훈: **운영에 바로 배포되는 변경(스키마·인프라)은 로컬에서 100% 재현 검증이 안 되면 위험**, **CD가 무중단(blue-green/health 선검증)이 아니면 기동 실패가 곧 장애**

---

## 4. 실시간 presence (입장/퇴장/접속자 수) [#12, #13]

**결정**: 별도 메시지 발행 없이 **STOMP 생명주기 이벤트**로 감지.
- `SessionSubscribeEvent`(`/topic/rooms/{id}` 구독) = 입장, `SessionDisconnectEvent` = 퇴장
- `PresenceService`가 **인메모리 `ConcurrentHashMap<roomId, Map<sessionId, name>>`** 로 추적 → 입장/퇴장 시스템 메시지 + 접속자 수를 브로드캐스트
- 입장/퇴장 알림은 **DB에 저장하지 않음**(휘발성)

**트레이드오프 / 한계 (★ 솔직하게)**
- 인메모리라 **서버 재시작 시 카운트 리셋**, **다중 인스턴스로 스케일아웃하면 부정확**(각 JVM이 자기 세션만 셈) → 그 시점에 Redis로 외부화 필요
- 비정상 종료 시 DISCONNECT가 heartbeat 타임아웃 뒤에 와서 잠깐 과대 집계

★ **면접 포인트**: "단일 서버 가정의 인메모리 상태가 왜 위험한가", "언제 Redis가 필요한가"를 한계와 함께 설명.

---

## 5. 자유게시판 — 트리(스레드) 구조 [#14]

- PC통신 게시판 특유의 **답글 트리**: `Post.parentId`(원글이면 null)
- 조회 시 전체를 `parentId`로 트리 빌드 → **DFS 평탄화 + depth 부여**(들여쓰기)
- **트레이드오프**: 현재는 전체를 메모리에서 트리로 만든다 → 글이 많아지면 비효율. 글 수가 적은 단계라 단순함 우선. (페이지네이션/materialized path는 향후 과제)

---

## 6. 그 외 일관 적용된 백엔드 관습
- **soft delete + `BaseEntity`**: `is_deleted`/`created_at`/`updated_at` 공통화, 물리 삭제 대신 플래그
- **계층 분리**: Controller(검증·매핑) / Service(트랜잭션·비즈니스) / Repository
- **전역 예외 처리**: `@RestControllerAdvice`로 도메인 예외 → 에러코드+메시지 응답 일관화
- **단위 테스트**: Mongo 비의존 로직(`PresenceService`, `PostService` 트리, `AuthService`, `JwtProvider`)은 Mockito/순수 단위테스트로 커버 (H2엔 임베디드 Mongo가 없어 통합테스트가 어려운 점을 우회)
- **닉네임 다양성**(#18): 같은 인덱스 대각선 조합(4가지) → 형용사/명사 독립 랜덤(24×24=576). 작은 변화지만 "조합 폭발"의 개념.

---

## 7. 프론트 측 변화 (요약 — 동생이 백엔드라 핵심만)
- 단일 `App.tsx`(425줄) → 컴포넌트/훅/lib **모듈 분리**
- **디자인 토큰**(Tailwind `terminal.*`) + shadcn 패턴(`cva`/`cn`)으로 스타일 일관화
- 회원 로그인/가입을 **터미널 CLI 단계 입력**(아이디→비번 마스킹→닉)으로 — 나머지 화면과 UX 일관
- 백엔드 API 계약(익명 세션/JWT/occupants/posts)에 맞춘 연동

---

## 8. 남은 과제 (TODO)
1. **채팅 무한 스크롤** — 현재 입장 시 최근 4건만. cursor 기반 페이지네이션 필요
2. **귓속말(1:1)** — STOMP `/user/queue`
3. **방 생성 관리자 권한** — 현재 방 CRUD API에 인증/권한 없음
4. **Redis 도입** — presence/세션 외부화(스케일아웃), STOMP external broker
5. **JWT_SECRET 운영 주입** — 현재 미설정 시 기본값(취약). GitHub secret + cd.yml 반영(`workflow` scope 필요)
6. **게시판 페이지네이션 / 글 삭제 API**
