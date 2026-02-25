# BE 서버 실제성/신뢰도 분석 보고서 (2026-02-25)

## 1. 미흡한 부분

### 1.1 로컬 실행과 운영 환경의 격차

- 로컬 기본 프로파일은 H2 기반(`application-local.yml`)이고 운영은 MySQL/RDS 기반(`application-dev.yml`)입니다.
- 결론: 로컬 정상 동작이 운영 DB 동작을 1:1로 보장하지 않습니다.

### 1.2 시크릿/자격증명 관리 리스크

- OAuth, JWT, AWS 접근 정보가 설정 파일 및 시크릿 모듈에 직접 포함된 구조입니다.
- 관련 경로:
  - `application/src/main/resources/application-local.yml`
  - `application/src/main/resources/application-dev.yml`
  - `ahachul_secret/application-test.yml` 등
- 결론: 보안/운영 감사 관점에서 신뢰도 하락 요인입니다.

### 1.3 외부 의존성 장애 전파 가능성

- 공공데이터 API, 소셜 OAuth, Redis, AWS 리소스에 대한 의존이 큽니다.
- 일부 경로는 fallback을 갖췄지만, 전체 API에 대해 장애 시 사용자 영향이 균일하게 제어되는 상태는 아닙니다.

### 1.4 컨테이너/배포 재현성의 한계

- 모듈별 Dockerfile은 있으나, API 기준의 로컬 통합 `docker-compose` 표준이 부재합니다.
- 배포 워크플로우는 존재하지만 일부 파이프라인 설정이 불안정하거나 수동 개입 전제가 있습니다.
- 예: `dev-api-deploy.yml`의 trigger는 `branches-ignore: '**'`로 자동 배포가 실질적으로 비활성에 가깝습니다.

### 1.5 관측성(Observability) 기준 부족

- `/health-check` 단일 문자열 응답은 최소 생존 확인만 가능하며, DB/Redis/외부 API 상태를 포함한 운영 가시성은 부족합니다.

## 2. 개선 포인트

### 2.1 시크릿 완전 분리

- 애플리케이션 설정에서 민감정보를 제거하고 Secret Manager/Parameter Store 기반 주입으로 전환
- Git 이력에 남은 민감정보는 회수(rotate/revoke) 계획과 함께 정리

### 2.2 환경 동등성 강화

- API + MySQL + Redis + (필요 시 mock external gateway) 통합 `docker-compose` 제공
- 로컬/CI/staging에서 동일 프로파일 계열로 부팅 검증

### 2.3 운영 준비도 게이트 도입

- 최소 게이트:
  - 앱 기동 + DB 마이그레이션 + Redis 연결 + 핵심 API 스모크
  - OAuth mock/실연동 분리 검증
  - 공공데이터 장애 시 fallback 계약 검증

### 2.4 관측 체계 확장

- readiness/liveness 분리
- API latency/error ratio/stale ratio 대시보드 및 알람 기준 고정

### 2.5 배포 파이프라인 정합성 개선

- 브랜치 트리거/롤백/배포 후 헬스 체크를 명시적으로 자동화
- 수동 SSM 실행 경로는 실패 시 자동 롤백 절차 포함

## 3. 개발 진행 (BE 권장 실행안)

### 3.1 Phase 0 (즉시)

1. 민감정보 노출 점검 및 키 로테이션 실행
2. 설정 파일에서 민감값 제거 계획 확정(환경변수/시크릿 저장소)
3. 배포 워크플로우 트리거 오동작 구간 정리

### 3.2 Phase 1 (단기: 로컬 실제성 확보)

1. API 표준 통합 `docker-compose` 추가(MySQL, Redis 포함)
2. `application-local`을 운영 DB 스키마와 가까운 프로파일로 보강
3. `make` 또는 스크립트로 one-command 기동/중단 제공

### 3.3 Phase 2 (중기: staging 신뢰도)

1. staging RDS/Redis에 대한 배포 및 스모크 자동화
2. 핵심 API E2E(로그인, 즐겨찾기 역/경로, 실시간 도착/시간표) 자동 검증
3. 장애 주입(fault injection) 기반 fallback 검증

### 3.4 Phase 3 (운영: 100% 신뢰에 근접)

1. 운영 알람/대시보드 + 온콜 기준 수립
2. 배포 전후 성능/에러율 비교 자동 리포트
3. 정기 게임데이(외부 API 장애, Redis 장애, DB 지연) 수행

## 4. 검증 결과

### 4.1 분석 근거

- 설정 파일 확인:
  - `application/src/main/resources/application-local.yml`
  - `application/src/main/resources/application-dev.yml`
  - `ahachul_secret/application-test.yml`
- 배포/운영 스크립트 확인:
  - `.github/workflows/dev-api-deploy.yml`
  - `.github/workflows/dev-batch-deploy.yml`
  - `.github/workflows/dev-consumer-deploy.yml`
  - `.github/workflows/dev-cron-deploy.yml`
- 컨테이너 구성 확인:
  - `application/Dockerfile`
  - `consumer/docker-compose.yml`
  - `scheduler/app.Dockerfile`

### 4.2 현재 신뢰도 평가 (정성)

- 기능 로직 신뢰도: 중간 이상
  - 이유: 최근 단위 테스트 및 핵심 API 보강이 누적됨
- 운영 실제성 신뢰도: 중간 이하
  - 이유: 환경 동등성, 시크릿 관리, 관측 체계, 배포 정합성의 공백

### 4.3 “100% 정상 동작” 선행 조건

1. 민감정보 완전 분리 및 로테이션 완료
2. 로컬/CI/staging 환경 동등성(Compose + 표준 프로파일) 확보
3. 배포 파이프라인 트리거/롤백/헬스체크 자동화
4. 핵심 경로 E2E + 외부 의존 장애 fallback 테스트 자동화
5. 관측 지표/알람/운영 대응 기준 문서화 및 실제 훈련
