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

## Addendum (Iteration 7)
- 추가 실패 run: `22112382829` (`pull_request`)
- 로그 핵심:
  - 여전히 `activeProfiles=[]` 상태
  - Flyway migration `V202306110345__update_lost.sql`가 동일한 H2 문법 오류로 실패
- 판단:
  - workflow step 환경변수만으로는 Gradle test worker JVM까지 profile 주입이 보장되지 않음
- 보정:
  - `tasks.withType<Test>`에 `spring.profiles.active=test` system property 강제 주입

## Addendum (Iteration 8)
- 추가 실패 run: `22112606186` (`pull_request`)
- 로그 핵심:
  - test worker 시작 커맨드에 `-Dspring.profiles.active=test` 존재
  - 실행 로그에 `The following 1 profile is active: "test"` 반복
  - 그럼에도 Flyway migration이 실행되어 H2 문법 오류로 테스트 대량 실패(164 중 103 fail)
- 판단:
  - profile 미적용 이슈는 해소되었고, 외부 주입 설정으로 인해 `spring.flyway.enabled`가 재활성화되는 환경 오염 가능성이 높음
- 보정:
  - `tasks.withType<Test>`에 `spring.flyway.enabled=false` system property 강제 주입

## Addendum (Iteration 9)
- 추가 실패 run: `22112919805` (`pull_request`)
- 로그 핵심:
  - Flyway 실패는 사라졌으나 `DataSourceScriptDatabaseInitializer` 경로에서 실패
  - core 모듈 `data.sql` 실행 중 `Table "TB_MEMBER" not found` 발생
- 판단:
  - secrets parse로 주입된 `SPRING_*` 환경변수가 test 설정(`sql.init.mode=never`)을 덮어써 SQL init이 재활성화됨
- 보정:
  - `pr-test.yml` Parse 단계에서 `SPRING_*` 키 전체 차단
  - `tasks.withType<Test>`에 `spring.sql.init.mode=never` system property 강제 주입

## Addendum (Iteration 10)
- 추가 실패 run: `22113129584` (`pull_request`)
- 로그 핵심:
  - `KakaoMemberClientImpl.<init>(KakaoMemberClientImpl.kt:32)`에서 `NullPointerException`
  - `oAuthProperties.client["kakao"]!!` 평가 시점 Bean 생성 실패
  - `AuthService`/`AuthController` 의존성으로 다수 테스트가 연쇄 실패
- 판단:
  - Parse secrets로 주입된 env가 OAuth 프로퍼티 바인딩까지 오염시켜 test 기본 설정 로딩 결과를 불안정하게 만듦
- 보정:
  - `pr-test.yml`에서 `Parse combined secrets` 단계 제거
  - PR Test는 repository 내부 `application-test.yml`만 기준으로 실행

## Addendum (Iteration 11)
- 추가 실패 run: `22113485237` (`pull_request`)
- 로그 핵심:
  - `Compile Test Kotlin`까지 정상 종료
  - `Test with Gradle` 단계가 25분 이상 `in_progress`로 지속되어 수동 취소
  - 상태 API/로그 API에서 완료 판정 가능한 결과를 반환하지 못함
- 판단:
  - 테스트 클래스 단위 Redis Testcontainer 반복 기동으로 실행 시간이 과도하게 증가했을 가능성이 높음
  - `--info` 상세 로그로 장시간 실행 시 추적 효율이 떨어짐
- 보정:
  - `application`/`consumer` 테스트 `ContainerTest`를 singleton 기동으로 변경 (JVM당 1회)
  - `pr-test.yml`에 `timeout-minutes: 45` 추가
  - 테스트 명령을 `./gradlew --no-daemon test`로 조정

## Addendum (Iteration 12)
- 추가 실패 run: `22114433524` (`pull_request`)
- 로그 핵심:
  - `copyTestSecret`가 `NO-SOURCE`로 실행되어 test secret 파일 미복사
  - `KakaoMemberClientImpl.kt:32` `NullPointerException` 재발
  - 다수 연쇄 실패 후 `OutOfMemoryError` 발생 (`89 tests completed, 52 failed`)
- 판단:
  - CI는 `../ahachul_secret`가 없으므로 외부 복사 기반 test profile 전략은 구조적으로 불안정
  - `.gitignore`의 `application-test.yml` 전역 ignore 규칙이 모듈별 test 설정 파일의 버전 관리를 막고 있었음
- 보정:
  - `.gitignore`에 모듈 test config 예외 경로 추가
  - `application/consumer/core/scheduler/src/test/resources/application-test.yml`를 민감정보 제거 더미값으로 저장소에 명시 추가
  - PR Test를 외부 secret 부재 조건에서도 독립 실행 가능하도록 정렬
