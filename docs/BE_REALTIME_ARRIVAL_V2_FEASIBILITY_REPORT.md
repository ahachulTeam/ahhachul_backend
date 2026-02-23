# 실시간 도착정보 V2 구현 가능성 보고서 (BE/Infra)

- 작성일: 2026-02-23
- 작성자: BE
- 범위: `/v2/trains/real-times` 신규 API + Redis 캐시/스테일 fallback + 관측 지표

## 1. 결론 (Go / No-Go)
- **Go (구현 가능)**
- 전제조건: Redis 운영 안정성 확보, 외부 API recptnDt/barvlDt 파싱 안정화, 경보 대시보드 선구축

## 2. 요구사항별 구현 가능성
1. API 계약(`generatedAt`, `isStale`, `confidenceLevel`, `trainRealTimes`)
- 가능. 기존 v1 유지 + v2 별도 핸들러 추가로 회귀 영향 최소화 가능.
2. ETA/신뢰도 계산 규칙
- 가능. 서버 시각 기준 `freshnessSec` 계산 후 `etaSec`, `etaMinDisplay` 산출 일관화 가능.
3. fallback(외부 실패 시 stale 캐시)
- 가능. STALE 캐시 별도 키 분리 + lock 키로 stampede 방지 가능.
4. 에러코드(704, 701)
- 가능. 도메인 예외 분기 및 기존 에러 모델 확장으로 처리 가능.

## 3. 구현 단계 제안
1. **Phase A (기능 뼈대)**
- `/v2/trains/real-times` 엔드포인트 및 DTO 추가
- freshness/eta/confidence 계산 모듈 분리
- feature flag `trainRealtimeV2Enabled` 도입 (기본 OFF)
2. **Phase B (캐시/복원력)**
- Redis HOT/STALE/LOCK 키 적용
- 외부 API 실패 시 stale fallback + 704 분기
3. **Phase C (관측/운영)**
- `train_realtime_quality_minute` 1분 배치 upsert
- latency/failure/stale/low-confidence 대시보드 + 알림 임계치 적용
4. **Phase D (릴리즈)**
- RPS 200 부하검증(p95<500ms)
- 5% → 25% → 100% 카나리

## 4. 기술 리스크 및 완화책
1. 외부 API 시각 포맷 편차/지연 누적
- 완화: 파싱 실패 시 fallback 즉시 전환, 파싱 에러 메트릭 분리
2. 캐시 스탬피드
- 완화: `TRAIN_RT_V2_LOCK`(TTL 3s) + single flight 보장
3. stale 데이터 장기 노출
- 완화: stale TTL 300s 제한 + `isStale=true`, `confidenceLevel=LOW` 강제
4. FE 표시 불일치
- 완화: 샘플 JSON 고정 계약 테스트 + 예외 케이스 스냅샷 테스트

## 5. 인프라 선행작업
1. Redis 메모리/eviction 정책 확인 (`allkeys-lru` 여부 포함)
2. Secret Manager 경유 외부 API 키 이관
3. 모니터링 대시보드 및 알람 규칙 사전 배포
4. 카나리 트래픽 제어 경로 준비(feature flag + 라우팅 정책)

## 6. FE/PM 협업 필요사항
1. PM: `isStale`, `confidenceLevel`별 사용자 문구 단일 소스 확정
2. FE: `LOW` 신뢰도 및 `704/701` 예외 UI 분기 계약 고정
3. BE: 필드 누락/에러케이스 샘플 JSON 제공 후 계약 테스트 동기화

## 7. 예상 일정 (기준: 1명 주도 + 리뷰 지원)
- A: 1일
- B: 1일
- C: 1일
- D: 0.5~1일
- 총 3.5~4일

## 8. 최종 제안
- 본 건은 **기능/운영 모두 구현 가능**하며, 위험은 관측/락/문구 일관성으로 관리 가능함.
- 우선순위는 `계산 규칙 고정` → `stale fallback` → `대시보드/알람` 순으로 진행 권장.
