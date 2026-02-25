# 역 시간표 대체 데이터 소스 연결 설계안 (MVP)

## 1. 미흡한 부분

- 현재 첫차/막차 및 전체 시간표는 단일 공공 API(`SearchSTNTimeTableByIDService`)에 강하게 의존한다.
- 특정 노선/역(예: 2호선 다수 구간)에서 원천이 `INFO-200`을 반환하면 BE는 정상 응답(`EMPTY`)만 제공하고, 데이터 복구 경로가 없다.
- 빈 결과도 캐시에 장시간 유지되면(기존 24h) 사용자 체감상 "항상 없음"으로 고착된다.

## 2. 개선 포인트

- 데이터 소스를 2단계로 이원화한다.
  - 1차: 실시간성 소스(현재 공공 API)
  - 2차: 정적/반정적 소스(배포 가능한 정규 시간표 데이터셋)
- 소스 상태를 응답 메타로 투명하게 노출한다.
  - `sourceDetails`에 `PRIMARY_API`, `FALLBACK_STATIC`(확장 예정) 구분
  - `guidanceMessage`를 FE 정책 문구와 1:1 매핑
- 빈 결과 캐시를 짧게 유지해 자동 회복 가능성을 높인다.
  - EMPTY TTL: 10분
  - 정상 데이터 TTL: 24시간

## 3. 개발 진행

### 3.1 데이터 계층

- 신규 포트 도입: `StationTimesFallbackReader`
  - 역할: 역코드/요일/상하행 기준 정적 시간표 조회
  - 저장소: 초기 MVP는 S3 JSON 또는 DB 테이블(`tb_station_timetable_fallback`) 중 택1
- 로더 배치
  - 일 1회(또는 수동) 데이터 동기화
  - 데이터 품질 체크: 역코드 매핑 누락, 시간 포맷, 상/하행 커버리지

### 3.2 BE 조회 순서

1. Redis 캐시 조회
2. Primary API 조회
3. Primary `INFO-200` 또는 오류 시 FallbackReader 조회
4. 둘 다 없으면 `EMPTY` 유지

### 3.3 응답/캐시 정책

- `EMPTY + FALLBACK_EMPTY`: "연동 지연"
- `EMPTY + PRIMARY_NO_DATA`: "미제공"
- 캐시 TTL
  - `stationTimes.size > 0`: 86400s
  - `stationTimes.size == 0`: 600s

### 3.4 운영/관측

- 메트릭
  - `station_times_primary_no_data_ratio`
  - `station_times_fallback_hit_ratio`
  - `station_times_empty_ratio_by_line`
- 알림
  - 특정 노선 `empty_ratio > 60%` 10분 지속 시 Warning
  - `fallback_hit_ratio` 급증 시 원천 API 품질 점검 알림

## 4. 검증 결과

- 이번 스프린트 반영 완료
  - 빈 시간표 캐시 TTL 단축(10분) 적용
  - FE 상태 라벨 분기(`미제공`/`일시 지연`) 적용
- 다음 스프린트 검증 항목
  - FallbackReader 적용 후 `2호선 샘플 30역` 기준 EMPTY 비율 개선
  - FE 문구/배지가 `sourceDetails`와 정확히 일치하는지 계약 테스트
  - 장애 주입(Primary API 강제 실패) 시 fallback 경로 자동 전환 확인
