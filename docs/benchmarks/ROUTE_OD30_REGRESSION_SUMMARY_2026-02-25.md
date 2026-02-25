# 길찾기 정렬/그래프 + 카카오 OD30 회귀 벤치 요약 (2026-02-25)

## 1. 미흡한 부분
- 라인 그래프를 DB id/삽입 순서에 의존해 연결하면서 실제 역 순서와 다른 간선이 생성됨.
- BALANCED 경로 선택과 응답 `estimatedMinutes`가 동일 수식이라, 환승 억제를 위해 점수를 높이면 시간 MAE가 같이 악화됨.
- 2호선 순환이 선형 연결로만 구성되어 일부 OD에서 과도 우회가 발생함.

## 2. 개선 포인트
- 라인 내 정렬 기준을 `stationCode` 우선으로 고정하고 중복/self-edge를 제거한다.
- 경로 선택 점수와 사용자 노출 시간 추정치를 분리한다.
- 2호선은 wrap-edge(시작/끝 역 연결)를 추가해 순환 경로를 반영한다.

## 3. 개발 진행
- 코드 변경
  - `application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt`
  - `application/src/test/kotlin/backend/team/ahachul_backend/api/station/application/service/StationServiceRouteSearchUnitTest.kt`
- 신규/보강 스크립트
  - `scripts/benchmark/od30.json`
  - `scripts/benchmark/collect-kakao-baseline-od30.mjs`
  - `scripts/benchmark/compare-ahhachul-vs-kakao-od30.mjs`
- 회귀 테스트
  - stationCode 정렬 기반 간선 생성 테스트 추가
  - 2호선 순환 wrap-edge 테스트 추가

## 4. 검증 결과
- baseline: `docs/benchmarks/kakao_od30_baseline_2026-02-25.json`
- 리포트
  - 초기: `docs/benchmarks/ahhachul_vs_kakao_od30_report_2026-02-25T03-52-57-855Z.json`
  - 1차 개선: `docs/benchmarks/ahhachul_vs_kakao_od30_report_2026-02-25T03-56-12-549Z.json`
  - 최종: `docs/benchmarks/ahhachul_vs_kakao_od30_report_2026-02-25T04-43-53-295Z.json`
- 핵심 지표
  - 초기: `routeMatchRate=0.5000`, `timeMae=10.220`, `transferMae=0.833`
  - 최종: `routeMatchRate=0.8667`, `timeMae=6.309`, `transferMae=0.033`

## 5. 남은 갭
- 미일치 4건: 왕십리→구로디지털단지, 수서→홍대입구, 김포공항→종로3가, 공덕→강남
- 공통 패턴: 광역/공항 노선(수인분당선/공항철도/서해선 계열) 비용 모델 단순화 한계
