# PM -> BE 기능명세서: 첫차/막차 + 도보시간 리스크 V1

- 작성일: 2026-02-23
- 기준 문서: `ahachul_data/docs/PM_SUBWAY_PUBLIC_DATA_DEEP_RESEARCH_2026-02-23.md`

## 1. API 스펙
1. `GET /v2/stations/times/last-train-risk`
2. Query
- `stationId` (Long, required)
- `subwayLineId` (Long, required)
- `upDownType` (`UP|DOWN`, required)
- `stationTimeWeekType` (`WEEKDAY|SATURDAY|HOLIDAY`, required)
- `walkingMinutes` (Int, required)

## 2. Response 200
```json
{
  "result": {
    "stationTimeWeekType": "WEEKDAY",
    "upDownType": "DOWN",
    "walkingMinutes": 15,
    "nowAt": "2026-02-23T23:10:00+09:00",
    "lastDepartureTime": "23:58:00",
    "minutesToLastTrain": 48,
    "isLastTrainRisk": false,
    "riskLevel": "SAFE",
    "message": "현재 기준 막차 여유가 있습니다."
  }
}
```

## 3. 계산 규칙
1. 방향별 시간표에서 `lastDepartureTime` 산출
2. `minutesToLastTrain` 계산
3. `walkingMinutes + 기본 대기 3분` 대비 위험도 판정
- SAFE: 여유
- WARN: 임박
- RISK: 위험/경과

## 4. 테스트 요구사항
1. 컨트롤러 docs 테스트 추가
2. 서비스 로직 단위 테스트 추가

## 5. 완료 조건
1. API 구현 완료
2. 테스트 PASS
3. 기존 `/v1/stations/times`, `/v2/stations/times/summary` 영향 없음
