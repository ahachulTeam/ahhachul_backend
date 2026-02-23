# PM -> BE 기능명세서: 빠른하차/출구 추천 V1

- 작성일: 2026-02-24
- 기준 문서: `ahachul_data/docs/PM_SUBWAY_PUBLIC_DATA_DEEP_RESEARCH_2026-02-23.md`
- 목표: 홈 화면에서 상/하행 기준 빠른하차 칸 + 추천 출구를 즉시 안내

## 1. API 스펙
1. `GET /v2/stations/quick-exits`
2. Query
- `stationId` (Long, required)
- `subwayLineId` (Long, required)
- `upDownType` (`UP|DOWN`, required)
3. Response 200
```json
{
  "result": {
    "stationId": 622,
    "subwayLineId": 3,
    "upDownType": "DOWN",
    "recommendations": [
      {
        "carNo": "5-2",
        "exitNo": "3",
        "directionHint": "환승 통로 우측",
        "walkingBenefitMinutes": 2,
        "confidenceLevel": "MEDIUM"
      }
    ]
  }
}
```

## 2. 계산/선정 규칙
1. 1차는 Rule-based 추천으로 시작(외부 API 연동 전)
2. 역/노선/상하행 기준으로 최대 2개 추천 반환
3. `walkingBenefitMinutes`는 1~4분 범위의 체감 단축값
4. `confidenceLevel`은 `HIGH|MEDIUM|LOW` 중 하나

## 3. 비기능 요구사항
1. 기존 API 호환성 유지
2. 장애 시 빈 배열 반환 대신 기본 추천 1건 제공(안전 fallback)
3. 컨트롤러 docs 테스트 추가

## 4. 테스트 요구사항
1. 컨트롤러 docs 테스트에 신규 endpoint 추가
2. 추천 계산 유틸(또는 서비스) 단위 테스트 추가
3. 정상 응답 1케이스 이상 보장

## 5. 완료 조건 (DoD)
1. `/v2/stations/quick-exits` 구현
2. 테스트 PASS
3. FE 연동 가능한 응답 계약 확정
4. 문서/코드 주석은 한국어 유지
