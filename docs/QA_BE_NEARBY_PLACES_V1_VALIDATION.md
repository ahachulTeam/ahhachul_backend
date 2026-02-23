# QA 검수 리포트: BE 주변 식사/편의시설 추천 V1

- 검수일시: 2026-02-24
- 대상 커밋: 2c18387fea4949273db2c539a8e68999b67b2822

## 1. 실행 결과
- BE 단위/컨트롤러 테스트: PASS
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
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-52/application/src/test/kotlin/backend/team/ahachul_backend/api/member/adapter/web/in/AuthLogoutControllerTest.kt: (47, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-52/application/src/test/kotlin/backend/team/ahachul_backend/api/report/application/service/LostPostReportServiceTest.kt: (89, 13): Variable 'result' is never used
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-52/application/src/test/kotlin/backend/team/ahachul_backend/api/station/application/service/StationServiceTest.kt: (353, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-52/application/src/test/kotlin/backend/team/ahachul_backend/config/controller/CommonDocsTestConfig.kt: (63, 21): Unchecked cast: Nothing? to T

> Task :application:compileTestJava NO-SOURCE
> Task :application:testClasses

> Task :application:test
DEBUG 26-02-24 00:21:41[SpringApplicationShutdownHook] [GenericWebApplicationContext:1031] - Closing org.springframework.web.context.support.GenericWebApplicationContext@71978f46, started on Tue Feb 24 00:21:40 KST 2026
DEBUG 26-02-24 00:21:41[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:365] - Stopping beans in phase -2147483647
DEBUG 26-02-24 00:21:41[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:238] - Bean 'springBootLoggingLifecycle' completed its stop procedure
DEBUG 26-02-24 00:21:41[SpringApplicationShutdownHook] [ThreadPoolTaskExecutor:218] - Shutting down ExecutorService 'applicationTaskExecutor'

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
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:3:import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:4:import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:10:class StationNearbyPlacesController(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:11:    private val stationNearbyPlacesUseCase: StationNearbyPlacesUseCase,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:14:    @GetMapping("/v2/stations/nearby-places")
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/StationNearbyPlacesController.kt:15:    fun getNearbyPlaces(request: GetStationNearbyPlacesDto.Request): CommonResponse<GetStationNearbyPlacesDto.Response> {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:3:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:5:class GetStationNearbyPlacesDto {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:13:        fun toCommand(): GetStationNearbyPlacesCommand {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:14:            return GetStationNearbyPlacesCommand(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:35:        val supportsEnglishMenu: Boolean,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:36:        val confidenceLevel: NearbyPlaceConfidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/adapter/in/dto/GetStationNearbyPlacesDto.kt:39:    enum class NearbyPlaceConfidenceLevel {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:3:import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:4:import backend.team.ahachul_backend.api.station.application.port.`in`.StationNearbyPlacesUseCase
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:5:import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationNearbyPlacesCommand
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:11:class StationNearbyPlacesService : StationNearbyPlacesUseCase {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:13:    override fun getNearbyPlaces(command: GetStationNearbyPlacesCommand): GetStationNearbyPlacesDto.Response {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:14:        val places = StationNearbyPlacesRecommendationGenerator.generate(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesService.kt:21:        return GetStationNearbyPlacesDto.Response(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:3:import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationNearbyPlacesDto
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:5:object StationNearbyPlacesRecommendationGenerator {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:12:        val supportsEnglishMenu: Boolean,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:13:        val confidenceLevel: GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:18:            SeedPlace("안암 김밥스테이션", "분식", 4, true, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:19:            SeedPlace("한밤 편의마트", "편의점", 3, true, false, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.HIGH),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:20:            SeedPlace("역앞 샌드랩", "샌드위치", 6, true, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:21:            SeedPlace("24시 라면포차", "분식", 7, false, true, GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW),
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:30:    ): List<GetStationNearbyPlacesDto.Place> {
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:35:            GetStationNearbyPlacesDto.Place(
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:40:                supportsEnglishMenu = it.supportsEnglishMenu,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:60:                supportsEnglishMenu = false,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:61:                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.MEDIUM,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:68:                supportsEnglishMenu = true,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:69:                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:76:                supportsEnglishMenu = true,
application/src/main/kotlin/backend/team/ahachul_backend/api/station/application/service/StationNearbyPlacesRecommendationGenerator.kt:77:                confidenceLevel = GetStationNearbyPlacesDto.NearbyPlaceConfidenceLevel.LOW,
```

## 4. 판정
- 최종: 통과
