# BE 구현 기록: 역/호선 커뮤니티 신뢰 신호 (2026-02-26)

## 1. 미흡한 부분

1. `/v2/community/delay-signals`는 호선 단위 집계만 제공해 역 커뮤니티 범위에 직접 대응하지 못했다.
2. 동일 시간대 다중 제보(군집) 여부를 나타내는 메타/배지 필드가 없었다.

## 2. 개선 포인트

1. 요청 파라미터에 `stationId`를 추가해 역 범위 집계를 지원한다.
2. 응답에 `timeSlotMinutes`, `reliabilityBadgeLevel`, `sameTimeSlotSignalCount`, `sameTimeSlotDistinctAuthors`를 추가한다.
3. 기존 `confidenceLevel` 규칙은 유지하고, 군집 신호는 별도 배지로 노출한다.

## 3. 개발 진행

1. 요청/응답 DTO 확장
- `DelayProofDto.GetCommunityDelaySignalsRequest`
  - `stationId: Long?` 추가
- `DelayProofDto.GetCommunityDelaySignalsResponse`
  - `stationId`, `timeSlotMinutes`, `reliabilityBadgeLevel`
  - `sameTimeSlotSignalCount`, `sameTimeSlotDistinctAuthors` 추가

2. 커맨드 확장
- `GetCommunityDelaySignalsCommand`
  - `stationId: Long?` 추가

3. 서비스 로직 확장 (`DelayProofService`)
- `summarizeCommunitySignals(...)`에 `stationId` 인자 추가
- 조회 조건 `GetSliceCommunityPostCommand.stationId`에 반영
- 10분 슬롯 기준 군집 계산 추가
  - `SPIKE`: 슬롯 제보 >=6 && 작성자 >=4
  - `ELEVATED`: 슬롯 제보 >=3 && 작성자 >=2
  - 그 외 `NONE`
- `createDelayProof`, `getDelayCenterOverview`, `getCommunityDelaySignals` 호출부에 station scope 반영

4. 문서 테스트 갱신
- `DelayProofControllerDocsTest`
  - `stationId` query parameter 문서화
  - 신규 응답 필드 5개 문서화

## 4. 검증 결과

1. `./gradlew --no-daemon :application:test --tests '*DelayProofControllerDocsTest'` 통과
2. API 계약 기준으로 station scope + 동일 시간대 신뢰 배지 메타가 응답에 포함됨을 확인
