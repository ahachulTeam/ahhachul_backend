# Backend Conventions (ahhachul_backend)

## Commit Message

- Source of truth: backend `README.md` 협업 규칙
- Format: `gitmoji <commit message> (#issue number)`
- Example: `:memo: 거버넌스 문서 추가 (#123)`

## Branch Strategy

- Upstream branch model:
  - `main`
  - `develop`
  - `feature/<#issue number>`
  - `hotfix`
- Codex task execution model:
  - target branch: `codex/main`
  - working branch: `codex/*` (태스크별 분리)
  - PR: 레포별 분리 + 한국어 본문 + assignee `createhb21`

## Architecture Rules

- 헥사고날 + 멀티모듈(`core/application/scheduler/consumer`) 유지
- API 응답은 `CommonResponse`, 에러코드는 `ResponseCode` 단일 체계 유지
- DTO -> `toCommand(...)` 변환 패턴 유지
- DB 변경은 Flyway 마이그레이션으로만 반영
- Secret 하드코딩 금지 (`ahachul_secret`/env)

## Safety

- 회사 AWS default 계정 사용 금지
- 인프라 명령 전 `aws sts get-caller-identity` 필수
