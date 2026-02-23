# QA 검수 리포트: BE 막차 리스크 V1

- 검수일시: 2026-02-24
- 대상 커밋: 8711bf96aa99d11f6be86c063e312ffbf9ce03ce

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
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-40/application/src/test/kotlin/backend/team/ahachul_backend/api/member/adapter/web/in/AuthLogoutControllerTest.kt: (47, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-40/application/src/test/kotlin/backend/team/ahachul_backend/api/report/application/service/LostPostReportServiceTest.kt: (89, 13): Variable 'result' is never used
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-40/application/src/test/kotlin/backend/team/ahachul_backend/api/station/application/service/StationServiceTest.kt: (353, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-40/application/src/test/kotlin/backend/team/ahachul_backend/config/controller/CommonDocsTestConfig.kt: (63, 21): Unchecked cast: Nothing? to T

> Task :application:compileTestJava NO-SOURCE
> Task :application:testClasses

> Task :application:test
DEBUG 26-02-24 00:03:26[SpringApplicationShutdownHook] [GenericWebApplicationContext:1031] - Closing org.springframework.web.context.support.GenericWebApplicationContext@6f3e19b3, started on Tue Feb 24 00:03:24 KST 2026
DEBUG 26-02-24 00:03:26[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:365] - Stopping beans in phase -2147483647
DEBUG 26-02-24 00:03:26[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:238] - Bean 'springBootLoggingLifecycle' completed its stop procedure
DEBUG 26-02-24 00:03:26[SpringApplicationShutdownHook] [ThreadPoolTaskExecutor:218] - Shutting down ExecutorService 'applicationTaskExecutor'

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
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:6:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:68:    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesLastTrainRiskApiGet")
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:69:    override fun getLastTrainRisk(command: GetStationLastTrainRiskCommand): GetStationTimesDto.LastTrainRiskResponse {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:83:        val calculated = StationLastTrainRiskCalculator.calculate(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:86:            walkingMinutes = command.walkingMinutes,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:89:        return GetStationTimesDto.LastTrainRiskResponse(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:92:            walkingMinutes = command.walkingMinutes,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:96:            isLastTrainRisk = calculated.isLastTrainRisk,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:97:            riskLevel = calculated.riskLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:164:    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:165:        command: GetStationLastTrainRiskCommand, e: RedisConnectionFailureException
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:166:    ): GetStationTimesDto.LastTrainRiskResponse {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:174:    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:175:        command: GetStationLastTrainRiskCommand, e : CallNotPermittedException
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:176:    ): GetStationTimesDto.LastTrainRiskResponse {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationService.kt:177:        logger.error("circuit breaker opened for external station times last-train-risk api")
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:3:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:66:    data class LastTrainRiskRequest(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:71:        val walkingMinutes: Int,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:73:        fun toCommand(): GetStationLastTrainRiskCommand {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:74:            return GetStationLastTrainRiskCommand(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:79:                walkingMinutes = walkingMinutes,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:84:    data class LastTrainRiskResponse(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:87:        val walkingMinutes: Int,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:91:        val isLastTrainRisk: Boolean,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:92:        val riskLevel: LastTrainRiskLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationTimesDto.kt:96:    enum class LastTrainRiskLevel {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:26:    @GetMapping("/v2/stations/times/last-train-risk")
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:27:    fun getLastTrainRisk(request: GetStationTimesDto.LastTrainRiskRequest): CommonResponse<GetStationTimesDto.LastTrainRiskResponse> {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationController.kt:28:        val result = stationUseCase.getLastTrainRisk(request.toCommand())
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:8:object StationLastTrainRiskCalculator {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:15:        val riskLevel: GetStationTimesDto.LastTrainRiskLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:18:        val isLastTrainRisk: Boolean
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:19:            get() = riskLevel != GetStationTimesDto.LastTrainRiskLevel.SAFE
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:22:    fun calculate(nowAt: OffsetDateTime, lastDepartureTime: String?, walkingMinutes: Int): Result {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:23:        val safeWalkingMinutes = walkingMinutes.coerceAtLeast(0)
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:29:                riskLevel = GetStationTimesDto.LastTrainRiskLevel.RISK,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:39:                riskLevel = GetStationTimesDto.LastTrainRiskLevel.RISK,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:54:        val riskLevel = when {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:55:            minutesToLastTrain <= 0 -> GetStationTimesDto.LastTrainRiskLevel.RISK
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:56:            minutesToLastTrain <= requiredMinutes -> GetStationTimesDto.LastTrainRiskLevel.WARN
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:57:            else -> GetStationTimesDto.LastTrainRiskLevel.SAFE
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:60:        val message = when (riskLevel) {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:61:            GetStationTimesDto.LastTrainRiskLevel.SAFE -> "현재 기준 막차 여유가 있습니다."
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:62:            GetStationTimesDto.LastTrainRiskLevel.WARN -> "막차가 임박했습니다. 서둘러 이동해 주세요."
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:63:            GetStationTimesDto.LastTrainRiskLevel.RISK -> "지금 출발하면 막차 탑승이 어려울 수 있습니다."
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationLastTrainRiskCalculator.kt:69:            riskLevel = riskLevel,
```

## 4. 판정
- 최종: 통과
