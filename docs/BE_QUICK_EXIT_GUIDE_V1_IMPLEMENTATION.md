# BE 구현 기록: 빠른하차/출구 추천 V1

- 작성일: 2026-02-24
- 기반 명세: `docs/PM_TO_BE_QUICK_EXIT_GUIDE_V1_SPEC.md`

## 구현 내용
1. `GET /v2/stations/quick-exits` 엔드포인트 추가
2. 역/노선/상하행 기준 rule-based 빠른하차/출구 추천 계산기 추가
3. 컨트롤러 docs 테스트 및 계산기 단위 테스트 추가

## 응답 정책
- 최대 2건 추천 반환
- 데이터 소스가 없어도 기본 fallback 추천 1~2건 제공
- confidenceLevel은 `HIGH|MEDIUM|LOW`로 노출
