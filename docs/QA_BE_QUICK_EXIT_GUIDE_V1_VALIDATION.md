# QA 검수 리포트: BE 빠른하차/출구 추천 V1

- 검수일시: 2026-02-24
- 대상 커밋: 0fcd8ea69caee9b1046c1e25677821128dbe5cd3

## 1. 실행 결과
- BE 계산/컨트롤러 테스트: PASS
- 기능 키워드 스캔: PASS

## 2. 테스트 로그 요약 (tail)

### BE
```
> Task :application:kaptKotlin
> Task :application:compileKotlin
> Task :application:compileJava NO-SOURCE
> Task :application:classes
> Task :application:jar SKIPPED
> Task :application:inspectClassesForKotlinIC
> Task :application:kaptGenerateStubsTestKotlin
> Task :application:kaptTestKotlin

> Task :application:compileTestKotlin
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-47/application/src/test/kotlin/backend/team/ahachul_backend/api/member/adapter/web/in/AuthLogoutControllerTest.kt: (47, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-47/application/src/test/kotlin/backend/team/ahachul_backend/api/report/application/service/LostPostReportServiceTest.kt: (89, 13): Variable 'result' is never used
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-47/application/src/test/kotlin/backend/team/ahachul_backend/api/station/application/service/StationServiceTest.kt: (353, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-47/application/src/test/kotlin/backend/team/ahachul_backend/config/controller/CommonDocsTestConfig.kt: (63, 21): Unchecked cast: Nothing? to T

> Task :application:compileTestJava NO-SOURCE
> Task :application:testClasses

> Task :application:test
DEBUG 26-02-24 00:14:09[SpringApplicationShutdownHook] [GenericWebApplicationContext:1031] - Closing org.springframework.web.context.support.GenericWebApplicationContext@2c7a8af2, started on Tue Feb 24 00:14:07 KST 2026
DEBUG 26-02-24 00:14:09[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:365] - Stopping beans in phase -2147483647
DEBUG 26-02-24 00:14:09[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:238] - Bean 'springBootLoggingLifecycle' completed its stop procedure
DEBUG 26-02-24 00:14:09[SpringApplicationShutdownHook] [ThreadPoolTaskExecutor:218] - Shutting down ExecutorService 'applicationTaskExecutor'

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.6.1/userguide/command_line_interface.html#sec:command_line_warnings

Execution optimizations have been disabled for 3 invalid unit(s) of work during this build to ensure correctness.
Please consult deprecation warnings for more details.

BUILD SUCCESSFUL in 19s
17 actionable tasks: 17 executed
```

## 3. 스캔 결과
```
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:6:object StationQuickExitRecommendationCalculator {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:13:        val confidenceLevel: GetStationTimesDto.QuickExitConfidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:18:            Rule("5-2", "3", "환승 통로 우측", 2, GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:19:            Rule("4-4", "2", "엘리베이터 인접", 1, GetStationTimesDto.QuickExitConfidenceLevel.LOW),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:22:            Rule("6-1", "1", "계단 진입 즉시 좌측", 3, GetStationTimesDto.QuickExitConfidenceLevel.HIGH),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:23:            Rule("5-3", "2", "환승 통로 직진", 2, GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:31:    ): List<GetStationTimesDto.QuickExitRecommendation> {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:38:                GetStationTimesDto.QuickExitRecommendation(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:43:                    confidenceLevel = rule.confidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:63:                confidenceLevel = GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationQuickExitRecommendationCalculator.kt:70:                confidenceLevel = GetStationTimesDto.QuickExitConfidenceLevel.LOW,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:4:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:97:    data class QuickExitRequest(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:102:        fun toCommand(): GetStationQuickExitCommand {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:103:            return GetStationQuickExitCommand(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:111:    data class QuickExitResponse(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:115:        val recommendations: List<QuickExitRecommendation>,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:118:    data class QuickExitRecommendation(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:123:        val confidenceLevel: QuickExitConfidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:130:    enum class QuickExitConfidenceLevel {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:32:    @GetMapping("/v2/stations/quick-exits")
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:33:    fun getQuickExits(request: GetStationTimesDto.QuickExitRequest): CommonResponse<GetStationTimesDto.QuickExitResponse> {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:34:        val result = stationUseCase.getQuickExits(request.toCommand())
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:7:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:103:    override fun getQuickExits(command: GetStationQuickExitCommand): GetStationTimesDto.QuickExitResponse {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:104:        val recommendations = StationQuickExitRecommendationCalculator.recommend(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:110:        return GetStationTimesDto.QuickExitResponse(
```

## 4. 판정
- 최종: 통과
