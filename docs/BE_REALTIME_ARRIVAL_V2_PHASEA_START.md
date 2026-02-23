# BE 실시간 도착정보 V2 Phase A 시작

- 작성일: 2026-02-23
- 목표: `/v2/trains/real-times` 구현 시작점 마련

## 반영 내용
1. V2 DTO 추가 (`GetTrainRealTimesV2Dto`)
2. Controller에 `/v2/trains/real-times` 엔드포인트 추가
3. UseCase/Service에 `getTrainRealTimesV2` 메서드 추가
4. V1 데이터를 활용한 V2 응답 스캐폴딩(기본값 기반)

## 다음 단계
1. freshness/eta 계산 규칙을 외부 recptnDt 기준으로 정교화
2. Redis HOT/STALE/LOCK 적용
3. 704/701 및 stale fallback 완성
4. 품질 지표 집계/알람 연동
