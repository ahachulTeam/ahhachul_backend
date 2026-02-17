# Backend/DB/Infra Rulebook Changelog

## 2026-02-18
- `BACKEND_DB_INFRA_RULEBOOK.md` 최초 생성.
- `ahhachul_backend`, `ahachul_data`, `ahachul_secret` 기준 아키텍처/컨벤션/운영 규칙 통합.
- AWS 계정 안전 규칙(회사 default 계정 사용 금지) 명문화.
- 실행 점검용 `BACKEND_DB_INFRA_EXECUTION_CHECKLIST.md` 추가.
- `BACKEND_DB_INFRA_EXECUTION_CHECKLIST.md`에 backend local 표준 실행 명령(`:application:bootRun --spring.profiles.active=local`) 추가.
- `docs/governance`에 BE 운영 문서 세트(`BE_RULEBOOK`, `BE_VALIDATOR_CHECKLIST`, `BE_SPRINT_BOARD`, `BE_TEAM_STATUS`, `BE_HANDOFF_LOG`, `BE_WORKLOG`, `BE_MEETING_LOG`) 신설.
- `CONVENTIONS_BE.md`/`BACKEND_DB_INFRA_RULEBOOK.md`의 브랜치 정책을 `codex/main` + `codex/*` 체계로 동기화.
