# PR Test CI Stabilization Log

- scope: `.github/workflows/pr-test.yml`
- objective: Ensure PR test runs and passes on `codex/main` based pull requests.
- createdAt: 2026-02-18

## Iteration 1 (Fail)
- PR: #375 (`codex/pr-test-codex-main-20260218` -> `codex/main`)
- run: `22111491885`
- status: failed
- failure point: `Checkout source code`
- evidence:
  - `fatal: could not read Username for 'https://github.com': terminal prompts disabled`
  - custom token path (`token: ${{ env.TOKEN_GITHUB }}`) was used for primary checkout

## Deep Analysis Summary
- `actions/checkout` 기본 토큰은 `${{ github.token }}`이며, 현재 레포 접근에는 기본 토큰 사용이 더 안정적입니다.
- job-level `permissions`를 명시한 상태에서 `contents: read`가 빠지면 기본 토큰 read 권한이 보장되지 않습니다.
- JSON secrets를 그대로 `echo` 후 실행하는 패턴은 값 escaping/마스킹 측면에서 운영 리스크가 있습니다.

## Agreed Fixes
1. PR Test 트리거를 `codex/main`, `develop` 모두 대상으로 유지
2. checkout 토큰을 `${{ github.token }}`로 전환
3. job permissions에 `contents: read` 추가
4. Parse combined secrets 단계에 `::add-mask::` 적용 및 안전한 키/값 주입 방식 사용
5. PR Test에서 submodule checkout은 비활성화(`submodules: false`)하여 1차 안정성 확보

## Iteration 2 (Fail)
- run: `22111610850` (`push`)
- status: failed (jobs 0)
- observed symptom:
  - workflow name이 `PR Test`가 아닌 파일 경로(`.github/workflows/pr-test.yml`)로 표시
  - 실행 job이 생성되지 않음
- inferred root cause:
  - secrets context를 step-level `if:`에서 직접 참조한 구성이 workflow validation에서 문제를 유발

## Iteration 2 Fixes
1. step-level `if: ${{ secrets... }}` 제거
2. run script 내부에서 `DEV_API_META_DATA` empty guard 처리
3. checkout의 token 입력은 default behavior로 단순화

## Iteration 3 (Fail)
- run: `22111678992` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - `Could not find a valid Docker environment`
  - `client version 1.32 is too old. Minimum supported API version is 1.44`
- root cause:
  - Testcontainers(`1.18.1`)의 Docker client API 호환 범위가 현재 runner Docker 엔진과 불일치

## Iteration 3 Fixes
1. Testcontainers 버전 상향
   - `org.testcontainers:testcontainers:1.20.4`
   - `org.testcontainers:junit-jupiter:1.20.4`

## Iteration 4 (Fail)
- run: `22111846394` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - Docker route: `unix://localhost:2375`
  - `client version 1.32 is too old. Minimum supported API version is 1.44`
- root cause:
  - Docker/Testcontainers 관련 환경변수(`DOCKER_HOST`, `DOCKER_API_VERSION`)가 런타임에서 오염되어 잘못된 값으로 고정됨

## Iteration 4 Fixes
1. secrets parse 단계에서 아래 키는 주입 차단
   - `DOCKER_HOST`
   - `DOCKER_API_VERSION`
   - `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE`
2. Testcontainers 실행 전 Docker 런타임 변수 강제 정규화
   - `DOCKER_HOST=unix:///var/run/docker.sock`
   - `DOCKER_API_VERSION=1.44`
   - `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`
3. 디버깅 가시성 확보를 위해 Docker 관련 변수 출력 단계 추가

## Iteration 5 (Fail)
- run: `22111991079` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - Docker runtime 변수 출력은 정상 (`DOCKER_HOST=unix:///var/run/docker.sock`, `DOCKER_API_VERSION=1.44`)
  - Testcontainers 내부 클라이언트는 여전히 `client version 1.32`로 접속 시도
- inferred root cause:
  - Docker API 버전 설정이 테스트 JVM(system property)까지 전달되지 않음

## Iteration 5 Fixes
1. Gradle `tasks.withType<Test>`에 Test JVM system property 강제 주입
   - `api.version=1.44`
   - `DOCKER_HOST=unix:///var/run/docker.sock` (환경 변수 미설정 시 fallback)

## Verification Plan
1. 수정 커밋 푸시 후 PR Test 재실행
2. 실패 시 run 로그 기준 추가 보정
3. 연속 성공이 확인될 때까지 반복
