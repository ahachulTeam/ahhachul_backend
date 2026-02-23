# QA 검수 리포트: BE 실시간 confidence 계산 보강 V1

- 검수일시: 2026-02-23
- 대상 커밋: c468872254e2d45576753ac01e2100b4f26c2886

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
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-34/application/src/test/kotlin/backend/team/ahachul_backend/api/member/adapter/web/in/AuthLogoutControllerTest.kt: (47, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-34/application/src/test/kotlin/backend/team/ahachul_backend/api/report/application/service/LostPostReportServiceTest.kt: (89, 13): Variable 'result' is never used
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-34/application/src/test/kotlin/backend/team/ahachul_backend/api/station/application/service/StationServiceTest.kt: (353, 21): Unchecked cast: Nothing? to T
w: /Users/createahb21/Documents/Programming/repositories/ahhachul-orchestration/runtime/worktrees/be/task-34/application/src/test/kotlin/backend/team/ahachul_backend/config/controller/CommonDocsTestConfig.kt: (63, 21): Unchecked cast: Nothing? to T

> Task :application:compileTestJava NO-SOURCE
> Task :application:testClasses

> Task :application:test
DEBUG 26-02-23 23:49:09[SpringApplicationShutdownHook] [GenericWebApplicationContext:1031] - Closing org.springframework.web.context.support.GenericWebApplicationContext@4195105b, started on Mon Feb 23 23:49:06 KST 2026
DEBUG 26-02-23 23:49:09[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:365] - Stopping beans in phase -2147483647
DEBUG 26-02-23 23:49:09[SpringApplicationShutdownHook] [DefaultLifecycleProcessor:238] - Bean 'springBootLoggingLifecycle' completed its stop procedure
DEBUG 26-02-23 23:49:09[SpringApplicationShutdownHook] [ThreadPoolTaskExecutor:218] - Shutting down ExecutorService 'applicationTaskExecutor'

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.6.1/userguide/command_line_interface.html#sec:command_line_warnings

Execution optimizations have been disabled for 3 invalid unit(s) of work during this build to ensure correctness.
Please consult deprecation warnings for more details.

BUILD SUCCESSFUL in 26s
17 actionable tasks: 17 executed
```

## 3. 스캔 결과
```
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:85:        val meta = TrainRealtimeV2Calculator.resolveMeta(
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:87:            recptnAtRawList = rawTrainRealTimes.map { it.externalRecptnAt },
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:91:            val etaSec = TrainRealtimeV2Calculator.calculateEtaSec(train.rawEtaSec, meta.freshnessSec)
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:92:            val etaMinDisplay = TrainRealtimeV2Calculator.calculateEtaMinDisplay(etaSec)
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:109:            freshnessSec = meta.freshnessSec,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainService.kt:110:            confidenceLevel = meta.confidenceLevel,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:9:object TrainRealtimeV2Calculator {
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:13:        val freshnessSec: Int,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:14:        val confidenceLevel: String,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:25:        val freshnessSec = max((generatedAt.toEpochSecond() - lastExternalRecptnAt.toEpochSecond()).toInt(), 0)
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:29:            freshnessSec = freshnessSec,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:30:            confidenceLevel = resolveConfidenceLevel(freshnessSec),
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:34:    fun calculateEtaSec(rawEtaSec: Int, freshnessSec: Int): Int {
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:35:        return max(rawEtaSec - freshnessSec, 0)
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:45:    private fun resolveConfidenceLevel(freshnessSec: Int): String {
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:47:            freshnessSec <= 60 -> "HIGH"
application/src/main/kotlin/backend/team/ahachul_backend/api/train/application/service/TrainRealtimeV2Calculator.kt:48:            freshnessSec <= 120 -> "MEDIUM"
application/src/main/kotlin/backend/team/ahachul_backend/api/train/adapter/in/dto/GetTrainRealTimesDto.kt:28:        val rawEtaSec: Int = 0,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/adapter/in/dto/GetTrainRealTimesDto.kt:30:        val externalRecptnAt: String? = null,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/adapter/in/dto/GetTrainRealTimesDto.kt:46:                    rawEtaSec = it.barvlDt?.toIntOrNull() ?: fallbackEtaSec,
application/src/main/kotlin/backend/team/ahachul_backend/api/train/adapter/in/dto/GetTrainRealTimesDto.kt:47:                    externalRecptnAt = it.recptnDt,
```

## 4. 판정
- 최종: 통과
