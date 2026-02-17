# BE Team Visibility Guide

## Purpose
- BE 다역할 협업 상태를 단일 문서/파일 세트로 추적합니다.

## Source of Truth
- runtime status: `docs/BE_TEAM_STATUS.json`
- handoff history: `docs/BE_HANDOFF_LOG.ndjson`
- sprint backlog: `docs/BE_SPRINT_BOARD.md`
- milestone narrative: `docs/BE_WORKLOG.md`

## 빠른 확인 명령
```bash
cat docs/BE_TEAM_STATUS.json
tail -n 20 docs/BE_HANDOFF_LOG.ndjson
```

## Update Contract
역할/상태가 변경될 때마다 아래를 동시에 갱신합니다.
1. `BE_TEAM_STATUS.json`
2. `BE_HANDOFF_LOG.ndjson` 신규 라인 추가
3. 필요 시 `BE_WORKLOG.md` 마일스톤 기록 추가

