# BE 구현 기록: 실시간 confidence 계산 보강 V1

- 작성일: 2026-02-23
- 기반 명세: `docs/PM_TO_BE_REALTIME_CONFIDENCE_BADGE_V1_SPEC.md`

## 구현 내용
1. `/v2/trains/real-times`의 freshness/confidence 계산 로직 보강
2. 외부 수신시각(recptnDt) 파싱 유틸 추가
3. ETA 계산 시 freshness 반영
4. 계산 유틸 단위 테스트 추가

## 계산 규칙
- freshnessSec = now - lastExternalRecptnAt
- confidenceLevel: HIGH(<=60), MEDIUM(<=120), LOW(>120)
- etaSec = max(rawEtaSec - freshnessSec, 0)
