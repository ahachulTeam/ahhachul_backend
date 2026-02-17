# BE Meeting Record

- title: PR Test Workflow Stabilization
- datetimeKST: 2026-02-18 03:56:37
- participants:
  - BE Lead
  - BE Specialist A
  - BE Specialist B
  - DB/Infra Specialist
  - QA
  - Technical Writer
  - Perfectionist Validator
- related PR: `#375`
- related run: `22111491885`

## Agenda
1. `pr-test.yml`의 `codex/main` 대상 실행 실패 원인 분석
2. 인증/권한/시크릿 처리 개선안 확정
3. 재검증 루프 및 완료 기준 합의

## Evidence Reviewed
- 실패 로그:
  - `Checkout source code` 단계 실패
  - `fatal: could not read Username for 'https://github.com': terminal prompts disabled`
- 기존 워크플로 구성:
  - checkout token이 `${{ env.TOKEN_GITHUB }}`에 의존
  - job permissions에 `contents: read` 미포함
  - secrets parse가 값 masking 없이 동적 주입

## Discussion Summary
- BE Lead: 기본 레포 checkout은 `${{ github.token }}`을 우선 사용해야 안정적이라고 판단.
- DB/Infra Specialist: 권한 명시 시 `contents: read` 누락이 치명적일 수 있으므로 명시 필요.
- BE Specialist A: secrets 파싱은 key/value 단위로 마스킹 후 `GITHUB_ENV`에 주입하도록 변경 제안.
- QA: submodule은 PR test의 1차 게이트에서 비활성화하고, 필요 시 별도 integration gate로 분리하는 것이 추적에 유리.
- Validator: 단발 성공이 아닌 반복 성공을 완료 기준으로 승인.

## Decisions
1. `pr-test.yml` pull_request branches에 `codex/main` 유지
2. checkout action을 `actions/checkout@v4` + `token: ${{ github.token }}`로 변경
3. `permissions.contents: read` 추가
4. Parse secrets 단계에 `::add-mask::` 적용 및 안전한 주입 방식 채택
5. `submodules: false`로 설정해 checkout 경로 단순화

## Action Items
1. 워크플로 수정 커밋 푸시
2. PR Test 재실행
3. 실패 시 로그 기반 보정 반복
4. 연속 성공 확인 후 최종 보고

## Addendum (Iteration 2)
- 추가 실패 run: `22111610850` (`push`, jobs 0)
- 판단:
  - step-level `if`에서 secrets context 직접 참조가 workflow validation 리스크를 만들 수 있음
- 보정:
  - `if` 제거 후 run script 내부 empty guard로 대체
  - checkout token 설정 단순화(기본 토큰 경로)

## Addendum (Iteration 3)
- 추가 실패 run: `22111678992` (`pull_request`)
- 로그 핵심:
  - `Could not find a valid Docker environment`
  - `client version 1.32 is too old. Minimum supported API version is 1.44`
- 판단:
  - Testcontainers 버전(`1.18.1`)과 runner Docker 엔진 API 호환성 불일치
- 보정:
  - `org.testcontainers:testcontainers` 및 `junit-jupiter`를 `1.20.4`로 상향

## Addendum (Iteration 4)
- 추가 실패 run: `22111846394` (`pull_request`)
- 로그 핵심:
  - Docker route가 `unix://localhost:2375`로 고정
  - 여전히 `client version 1.32` 사용
- 판단:
  - Docker/Testcontainers 관련 환경변수가 런타임에 오염되어 잘못된 endpoint/api 버전 사용
- 보정:
  - Parse 단계에서 `DOCKER_*`, `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` 주입 차단
  - 테스트 전 Docker 런타임 변수 강제 정규화 및 값 출력 단계 추가

## Addendum (Iteration 5)
- 추가 실패 run: `22111991079` (`pull_request`)
- 로그 핵심:
  - 워크플로 환경 변수는 `DOCKER_HOST=unix:///var/run/docker.sock`, `DOCKER_API_VERSION=1.44`로 정상
  - Testcontainers 내부는 여전히 `client version 1.32`로 요청
- 판단:
  - Docker API 버전 값이 test JVM system property까지 전달되지 않아 docker-java 기본값 경로가 유지
- 보정:
  - `tasks.withType<Test>`에 `api.version` 및 `DOCKER_HOST` system property 강제 주입

## Addendum (Iteration 6)
- 추가 실패 run: `22112173940` (`pull_request`)
- 로그 핵심:
  - Testcontainers 관련 실패는 해소됨
  - Flyway migration `V202306110345__update_lost.sql`가 H2 문법 오류(`MODIFY ...`)로 실패
- 판단:
  - CI test step에서 `test` profile 미적용 상태로 실행되어 Flyway 경로 진입
- 보정:
  - `Test with Gradle` step에 `SPRING_PROFILES_ACTIVE=test` 명시
