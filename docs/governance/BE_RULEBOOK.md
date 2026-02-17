# BE Rulebook

Last Updated: 2026-02-18
Scope: `ahhachul_backend` (`core`, `application`, `scheduler`, `consumer`)
Owner: BE Lead + Technical Writer

## 1. 목적
- BE 구현/검증/PR 운영 규칙을 단일 소스로 관리합니다.
- `TEAM_TOPOLOGY`의 역할 분담을 BE 실행 절차에 직접 연결합니다.
- 태스크 종료 시 재현 가능한 근거(로그/체크리스트/PR 본문)를 남깁니다.

## 2. BE Pod 구조
- BE Lead: 백엔드 아키텍처/도메인 계약 최종 의사결정
- BE Specialist A: API/도메인 서비스/예외 처리 구현
- BE Specialist B: 데이터 계층/통합/운영성 개선 구현
- DB/Infra Specialist: 환경/데이터/운영 연동 및 배포 안정성
- QA: 회귀 시나리오/검증 항목 관리
- Technical Writer: 정책/결정/작업 기록 문서화
- Perfectionist Validator: 블로킹 게이트 최종 판정

## 3. Rulebook 운영 규칙
- 본 문서 수정 시 같은 커밋에 `BE_RULEBOOK_CHANGELOG.md`를 반드시 갱신합니다.
- BE 회의록은 `docs/governance/be-meetings`에 `YYYY-MM-DD_HHMMSS_<TOPIC>.md` 형식으로 저장합니다.
- 회의 파일 추가 시 `BE_MEETING_LOG.md` 인덱스를 동기화합니다.
- PR 기본 규칙:
  - base branch: `codex/main`
  - PR 본문 언어: 한국어
  - Assignee: `createhb21`
  - 레포별 PR 분리 유지

## 4. 설계/구현 규칙

### 4.1 API 계약
- API prefix는 `/v1/...`를 유지합니다.
- Controller 응답은 `CommonResponse` 계약을 유지합니다.
- Request DTO -> `toCommand(...)` 변환 패턴을 유지합니다.
- 기존 응답 필드/에러 코드 호환성을 깨는 변경은 금지합니다.

### 4.2 도메인/예외
- 예외는 레이어별 타입(`CommonException`, `AdapterException`, `PortException`, `DomainException`, `BusinessException`)을 유지합니다.
- 에러 코드는 `ResponseCode` 단일 소스를 사용합니다.
- 도메인 규칙은 service가 아닌 domain 모델/정책 객체에 우선 배치합니다.

### 4.3 데이터/Flyway
- DB 스키마 변경은 Flyway 마이그레이션으로만 반영합니다.
- 마이그레이션 파일명은 `VYYYYMMDDHHmm__description.sql` 패턴을 따릅니다.
- 시드 변경은 소비자(`consumer`) 영향 범위를 PR 본문에 기록합니다.

### 4.4 운영/보안
- 민감정보(토큰/키/계정식별자)는 코드/문서/PR 본문에 기록하지 않습니다.
- 실행 전 AWS 계정 확인(`aws sts get-caller-identity`)을 수행합니다.
- 로컬 실행 규칙은 표준 명령(`:application:bootRun --spring.profiles.active=local`)을 기준으로 합니다.

## 5. 검증 게이트
- `BE_VALIDATOR_CHECKLIST.md`의 모든 필수 항목을 통과해야 커밋/PR 단계로 이동합니다.
- Validator 미통과 상태에서는 태스크 상태를 `Rework`로 되돌립니다.
- 검증 로그/재현 절차/실패-성공 비교를 PR 본문에 기록합니다.

## 6. 실행 아티팩트
- 스프린트/태스크 상태: `BE_SPRINT_BOARD.md`
- 역할별 실시간 상태: `BE_TEAM_STATUS.json`
- 핸드오프 이력: `BE_HANDOFF_LOG.ndjson`
- 마일스톤 로그: `BE_WORKLOG.md`
- 블로킹 게이트: `BE_VALIDATOR_CHECKLIST.md`

