# PM -> BE 기능명세서: 실시간 도착 신뢰도 계산 보강 V1

- 작성일: 2026-02-23
- 기준 문서: `ahachul_data/docs/PM_SUBWAY_PUBLIC_DATA_DEEP_RESEARCH_2026-02-23.md`
- 목표: `/v2/trains/real-times`의 신뢰도 메타를 정적값이 아닌 계산값으로 제공

## 1. 구현 범위
1. `freshnessSec` 계산 보강
2. `confidenceLevel` 계산 규칙 반영
3. `etaSec` 계산 시 신선도 반영

## 2. 계산 규칙
1. `freshnessSec = now - lastExternalRecptnAt`
2. `confidenceLevel`
- HIGH: freshnessSec <= 60
- MEDIUM: 61~120
- LOW: >120
3. `etaSec = max(rawEtaSec - freshnessSec, 0)`

## 3. 제약
1. API 스펙 변경 없이 기존 필드 유지
2. v1 API 동작 영향 금지

## 4. 테스트 요구사항
1. 계산 로직 단위 테스트 추가
2. 기존 컨트롤러 docs 테스트 회귀 확인

## 5. 완료 조건
1. 계산 로직 반영
2. 테스트 PASS
3. 문서/주석 한국어 유지
