# BE 구현 기록: 역 첫차/막차 요약 V1

- 작성일: 2026-02-23
- 기반 명세: `docs/PM_TO_BE_SUBWAY_FIRST_LAST_SUMMARY_V1_SPEC.md`

## 구현 범위
1. 신규 API `GET /v2/stations/times/summary` 추가
2. 상행/하행별 첫차/막차 요약 계산
3. 기존 `/v1/stations/times` 및 캐시/외부호출 로직 재사용
4. StationController docs 테스트 확장

## 응답 요약
- `stationTimeWeekType`
- `summaries[]`
  - `upDownType`
  - `firstDepartureTime`
  - `lastDepartureTime`
  - `firstDestinationStationName`
  - `lastDestinationStationName`

## 비고
- 방향별 데이터가 비어 있으면 nullable 필드를 반환
