# BE 구현 기록: 첫차/막차 + 도보시간 리스크 V1

- 작성일: 2026-02-23
- 기반 명세: `docs/PM_TO_BE_LAST_TRAIN_RISK_V1_SPEC.md`

## 구현 내용
1. `GET /v2/stations/times/last-train-risk` 추가
2. 방향별 막차 시간 + 도보시간 기반 리스크 계산 로직 추가
3. 계산 유틸/컨트롤러 docs 테스트 추가

## 리스크 규칙
- SAFE: 여유 있음
- WARN: 임박
- RISK: 위험 또는 막차 경과
