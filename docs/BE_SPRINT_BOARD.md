# BE Sprint Board

## Status Legend
- `Todo`
- `InProgress`
- `QAReady`
- `ValidatorPass`
- `Committed`
- `PRCreated`
- `Done`
- `Rework`

## Team Roles
- BE Lead: 아키텍처/도메인 계약 최종 승인
- BE Specialist A: API/서비스/예외 흐름 구현
- BE Specialist B: 저장소/통합/운영성 개선 구현
- DB/Infra Specialist: 환경/배포/데이터 연동 안정화
- QA: 회귀 테스트 시나리오 관리
- Technical Writer: 정책/결정/작업 로그 관리
- Perfectionist Validator: 블로킹 게이트 판정

## Sprint Backlog
| ID | Sprint | Task | Owner | Status | Commit | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| BG-000 | 0 | BE 거버넌스 런타임 문서 세트 베이스라인 구축 | Technical Writer | InProgress | pending | BE_RULEBOOK/SPRINT_BOARD/VALIDATOR/HANDOFF/WORKLOG/MEETING 체계 신설 |
| BG-010 | 0 | BE 검증 게이트 명령 세트 고정 | QA | Todo | pending | 모듈별 최소 검증 명령 및 증빙 템플릿 확정 |
| BG-020 | 0 | BE 핸드오프 로그 운영 자동화 | BE Specialist B | Todo | pending | 상태 변경 시 JSON + NDJSON 동시 갱신 규칙 정착 |

## Rule
- 태스크는 `Perfectionist Validator`가 `BE_VALIDATOR_CHECKLIST.md` 통과를 확인해야 `Done`으로 이동할 수 있습니다.

