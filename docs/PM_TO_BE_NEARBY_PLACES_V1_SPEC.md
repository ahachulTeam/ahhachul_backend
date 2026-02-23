# PM -> BE 기능명세서: 주변 간단 식사/편의시설 추천 V1

- 작성일: 2026-02-24
- 기준 문서: `ahachul_data/docs/PM_SUBWAY_PUBLIC_DATA_DEEP_RESEARCH_2026-02-23.md`
- 목표: 홈에서 역 주변의 빠른 식사/편의시설 정보를 즉시 확인

## 1. API 스펙
1. `GET /v2/stations/nearby-places`
2. Query
- `stationId` (Long, required)
- `subwayLineId` (Long, required)
- `exitNo` (String, optional)
- `limit` (Int, optional, default=3, max=5)
3. Response 200
```json
{
  "result": {
    "stationId": 622,
    "subwayLineId": 3,
    "exitNo": "3",
    "places": [
      {
        "name": "김밥역",
        "category": "분식",
        "walkingMinutes": 4,
        "openNow": true,
        "supportsEnglishMenu": true,
        "confidenceLevel": "MEDIUM"
      }
    ]
  }
}
```

## 2. 선정 규칙
1. 1차는 rule-based 샘플 데이터 기반으로 시작
2. `limit` 범위는 1~5로 보정
3. `walkingMinutes`는 3~12분 범위
4. `confidenceLevel`은 `HIGH|MEDIUM|LOW`

## 3. 비기능 요구사항
1. endpoint 실패 시 빈 리스트 대신 fallback 데이터 최소 1건 반환
2. 컨트롤러 docs 테스트 추가
3. 향후 외부 지도 API 연동을 고려해 응답 구조 고정

## 4. 테스트 요구사항
1. 컨트롤러 docs 테스트 추가
2. 추천 생성기 단위 테스트 추가
3. 정상 응답 1케이스 보장

## 5. 완료 조건 (DoD)
1. `/v2/stations/nearby-places` 구현
2. 테스트 PASS
3. FE 연동 가능한 JSON 계약 확정
4. 문서/코드 주석은 한국어 유지
