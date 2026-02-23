# PM -> BE 기능명세서: 홈 역 카드 첫차/막차 요약 V1

- 작성일: 2026-02-23
- 기준 문서: `ahachul_data/docs/PM_SUBWAY_PUBLIC_DATA_DEEP_RESEARCH_2026-02-23.md`
- 목표: 홈 화면용 "상/하행 첫차/막차 요약" API 제공

## 1. 기능명
- **Station First/Last Summary API V1**

## 2. API 스펙
1. `GET /v2/stations/times/summary`
2. Query
- `stationId` (Long, required)
- `subwayLineId` (Long, required)
- `stationTimeWeekType` (`WEEKDAY|SATURDAY|HOLIDAY`, required)
3. Response 200
```json
{
  "result": {
    "stationTimeWeekType": "WEEKDAY",
    "summaries": [
      {
        "upDownType": "UP",
        "firstDepartureTime": "05:31:00",
        "lastDepartureTime": "23:58:00",
        "firstDestinationStationName": "대화",
        "lastDestinationStationName": "오금"
      },
      {
        "upDownType": "DOWN",
        "firstDepartureTime": "05:36:00",
        "lastDepartureTime": "23:54:00",
        "firstDestinationStationName": "수서",
        "lastDestinationStationName": "구파발"
      }
    ]
  }
}
```

## 3. 계산 규칙
1. 각 방향(UP/DOWN)별 시간표를 조회
2. `departureTime` 최소값 = 첫차
3. `departureTime` 최대값 = 막차
4. 방향별 데이터가 없으면 해당 값 `null`

## 4. 비기능 요구사항
1. 기존 `/v1/stations/times` 유지
2. 신규 API는 기존 캐시/외부호출 로직 재사용 우선
3. 장애 시 기존 station time 에러 코드 체계 준수

## 5. 테스트 요구사항
1. 컨트롤러 docs 테스트 추가/확장
2. 요청 파라미터/응답 필드 문서화
3. 최소 1개 정상 응답 케이스 검증

## 6. 완료 조건 (DoD)
1. `/v2/stations/times/summary` 구현
2. 테스트 PASS
3. FE 연동 가능한 JSON 계약 확정
4. 문서/코드 주석은 한국어 유지
