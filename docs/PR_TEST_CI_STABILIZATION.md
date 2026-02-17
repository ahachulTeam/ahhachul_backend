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

## Iteration 6 (Fail)
- run: `22112173940` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - Docker/Testcontainers 단계는 정상 통과
  - Flyway migration 실패: `V202306110345__update_lost.sql`
  - H2 syntax error: `MODIFY member_id BIGINT NULL`
- inferred root cause:
  - CI test 실행 시 `test` profile이 적용되지 않아(`activeProfiles=[]`) Flyway가 활성 경로로 진입

## Iteration 6 Fixes
1. `Test with Gradle` step에 `SPRING_PROFILES_ACTIVE=test` 명시
2. 테스트 실행 환경을 로컬 테스트 규약(`application-test.yml`, `flyway.enabled=false`)과 정렬

## Iteration 7 (Fail)
- run: `22112382829` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - 여전히 `activeProfiles=[]`
  - Flyway migration `V202306110345__update_lost.sql`가 H2 문법 오류로 실패
- inferred root cause:
  - workflow env 주입만으로는 test worker JVM까지 profile 값이 안정적으로 전달되지 않음

## Iteration 7 Fixes
1. Gradle `tasks.withType<Test>`에 `spring.profiles.active=test` system property 강제 주입
2. test worker JVM 수준에서 profile 확정

## Iteration 8 (Fail)
- run: `22112606186` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - test worker 시작 커맨드에 `-Dspring.profiles.active=test`가 명시됨
  - 로그에 `The following 1 profile is active: "test"` 반복 출력
  - 그럼에도 Flyway migration `V202306110345__update_lost.sql` 실행 및 H2 문법 오류로 103개 테스트 실패
- inferred root cause:
  - test profile 활성화는 되었지만, 외부 주입 설정(예: secrets 기반 `SPRING_*`)에 의해 `spring.flyway.enabled`가 다시 활성화되는 환경 오염 발생

## Iteration 8 Fixes
1. Gradle `tasks.withType<Test>`에 `spring.flyway.enabled=false` system property 강제 주입
2. CI secrets 오염 여부와 무관하게 test JVM에서 Flyway 비활성 상태를 보장

## Iteration 9 (Fail)
- run: `22112919805` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - Flyway 오류는 사라짐
  - `DataSourceScriptDatabaseInitializer`가 core 모듈 `data.sql` 실행 시도
  - `Table "TB_MEMBER" not found`로 초기화 실패
- inferred root cause:
  - secrets parse로 주입된 `SPRING_*` 환경변수가 test 설정(`spring.sql.init.mode=never`, `ddl-auto=create-drop`)을 덮어써 SQL init 경로가 재활성화됨

## Iteration 9 Fixes
1. `pr-test.yml`의 Parse secrets 단계에서 `SPRING_*` 키 전체 주입 차단
2. Gradle `tasks.withType<Test>`에 `spring.sql.init.mode=never` system property 강제 주입

## Iteration 10 (Fail)
- run: `22113129584` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - `KakaoMemberClientImpl.<init>(KakaoMemberClientImpl.kt:32)`에서 `NullPointerException`
  - `oAuthProperties.client["kakao"]!!` 경로에서 Bean 생성 실패
  - `AuthService`/`AuthController` 의존성 연쇄로 다수 테스트 실패
- inferred root cause:
  - Parse secrets에서 주입된 대량 env가 OAuth 설정 바인딩을 오염시켜 테스트 기본 설정(`application-test.yml`)의 `oauth.client.kakao`가 정상 로드되지 않음

## Iteration 10 Fixes
1. `pr-test.yml`에서 `Parse combined secrets` 단계 제거
2. PR 테스트는 저장소 내 test 설정만 사용하도록 고정해 환경 오염 경로 제거

## Iteration 11 (Fail: Stuck)
- run: `22113485237` (`pull_request`)
- status: cancelled (manual, after prolonged in-progress)
- failure point: `Test with Gradle` 단계 장기 정체
- evidence:
  - `Compile Test Kotlin` 완료 후 `Test with Gradle`가 25분 이상 `in_progress` 유지
  - job/log API에서 진행 로그를 제공하지 못한 채 상태가 고정
- inferred root cause:
  - 테스트 클래스 단위 Redis Testcontainer 반복 기동으로 전체 수행 시간이 급증
  - `--info` 기반 과다 로그 출력이 장기 실행 시 관찰/회수 효율을 저하시킴

## Iteration 11 Fixes
1. Redis Testcontainer 기동 전략 개선
   - `application`, `consumer` 테스트 `ContainerTest`를 JVM당 1회 singleton 기동으로 변경
   - 클래스별 재기동 제거로 테스트 컨텍스트/컨테이너 churn 감소
2. `pr-test.yml` 실행 안정화
   - job `timeout-minutes: 45` 추가
   - 테스트 명령을 `./gradlew --no-daemon test`로 변경 (`--info` 제거)

## Iteration 12 (Fail)
- run: `22114433524` (`pull_request`)
- status: failed
- failure point: `Test with Gradle`
- evidence:
  - `copyTestSecret`가 `NO-SOURCE`로 실행되어 테스트 시크릿 파일이 복사되지 않음
  - `KakaoMemberClientImpl.kt:32`에서 `NullPointerException` 재발 (`oauth.client.kakao` 미바인딩)
  - 연쇄 실패 중 `OutOfMemoryError` 발생 (`89 tests completed, 52 failed`)
- inferred root cause:
  - CI 환경에는 `../ahachul_secret`가 없어 test profile 설정 파일을 외부 복사에 의존하면 항상 불완전 상태가 됨
  - `.gitignore`의 `application-test.yml` 전역 패턴으로 모듈별 `src/test/resources/application-test.yml` 버전 관리가 차단됨

## Iteration 12 Fixes
1. `.gitignore`에 모듈 테스트 설정 파일 예외 규칙 추가
   - `application/consumer/core/scheduler/src/test/resources/application-test.yml`
2. 위 4개 경로에 민감정보 제거된 표준 `application-test.yml` 추가
   - `oauth`, `jwt`, `public-data`, `cloud.aws.credentials` 등 필수 바인딩 키를 더미 값으로 고정
3. PR Test가 `ahachul_secret` 부재 환경에서도 독립적으로 부팅/바인딩되도록 정렬

## Iteration 13 (Success)
- run: `22114860183` (`pull_request`)
- result:
  - attempt 1: success
  - attempt 2 (manual rerun): success
- evidence:
  - `:application:test` 통과
  - `:consumer:test` 통과
  - 최종 `BUILD SUCCESSFUL`
- conclusion:
  - `ahachul_secret` 부재 환경에서도 test profile 바인딩 불안정성이 해소됨
  - 동일 커밋 연속 성공으로 PR Test 안정화 기준 충족

## Verification Plan
1. 수정 커밋 푸시 후 PR Test 재실행
2. 실패 시 run 로그 기준 추가 보정
3. 연속 성공이 확인될 때까지 반복
