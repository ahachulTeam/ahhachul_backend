package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesFullCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.PriorityQueue

@Service
@Transactional(readOnly = true)
class StationService(
    private val subwayLineStationReader: SubwayLineStationReader,
    private val stationTimesCacheUtils: StationTimesCacheUtils,
    private val seoulTrainClient: SeoulTrainClient,
): StationUseCase {

    private val logger: Logger = Logger(javaClass)

    private data class GraphEdge(
        val fromStationId: Long,
        val toStationId: Long,
        val subwayLineId: Long,
        val subwayLineName: String,
    )

    private data class RouteState(
        val stationId: Long,
        val lineId: Long?,
    )

    private data class RouteMetric(
        val stops: Int,
        val transfers: Int,
    )

    private data class StateWithMetric(
        val state: RouteState,
        val metric: RouteMetric,
    )

    private data class Prev(
        val previousState: RouteState,
        val edge: GraphEdge,
    )

    private data class PathResult(
        val nodes: List<Long>,
        val edges: List<GraphEdge>,
        val metric: RouteMetric,
        val stationNamesById: Map<Long, String>,
    )

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesApiGet")
    override fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response {
        return GetStationTimesDto.Response(loadStationTimes(command))
    }

    override fun getStationTimesFull(command: GetStationTimesFullCommand): GetStationTimesDto.FullResponse {
        val weeks = StationTimeWeekType.values().map { weekType ->
            val upDownTimetables = UpDownType.values().map { upDownType ->
                val stationTimes = loadStationTimesSafely(
                    GetStationTimesCommand(
                        stationId = command.stationId,
                        subwayLineId = command.subwayLineId,
                        upDownType = upDownType,
                        stationTimeWeekType = weekType,
                    )
                ).sortedBy { it.departureTime }

                GetStationTimesDto.UpDownTimetable(
                    upDownType = upDownType,
                    stationTimes = stationTimes,
                )
            }

            GetStationTimesDto.WeekTimetable(
                stationTimeWeekType = weekType,
                upDownTimetables = upDownTimetables,
            )
        }

        return GetStationTimesDto.FullResponse(
            generatedAt = OffsetDateTime.now().toString(),
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            weeks = weeks,
        )
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesSummaryApiGet")
    override fun getStationTimesSummary(command: GetStationTimesSummaryCommand): GetStationTimesDto.SummaryResponse {
        val summaries = UpDownType.values().map { upDownType ->
            val stationTimes = loadStationTimesForSummary(
                GetStationTimesCommand(
                    stationId = command.stationId,
                    subwayLineId = command.subwayLineId,
                    upDownType = upDownType,
                    stationTimeWeekType = command.stationTimeWeekType,
                )
            )
            val sortedByDepartureTime = stationTimes.sortedBy { it.departureTime }
            val firstTrain = sortedByDepartureTime.firstOrNull()
            val lastTrain = sortedByDepartureTime.lastOrNull()

            GetStationTimesDto.UpDownSummary(
                upDownType = upDownType,
                firstDepartureTime = firstTrain?.departureTime,
                lastDepartureTime = lastTrain?.departureTime,
                firstDestinationStationName = firstTrain?.arrivalStationName,
                lastDestinationStationName = lastTrain?.arrivalStationName,
            )
        }

        return GetStationTimesDto.SummaryResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            summaries = summaries,
        )
    }

    private fun loadStationTimesForSummary(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times summary fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesLastTrainRiskApiGet")
    override fun getLastTrainRisk(command: GetStationLastTrainRiskCommand): GetStationTimesDto.LastTrainRiskResponse {
        val stationTimes = loadStationTimesForLastTrainRisk(
            GetStationTimesCommand(
                stationId = command.stationId,
                subwayLineId = command.subwayLineId,
                upDownType = command.upDownType,
                stationTimeWeekType = command.stationTimeWeekType,
            )
        )

        val lastDepartureTime = stationTimes
            .maxByOrNull { it.departureTime }
            ?.departureTime

        val calculated = StationLastTrainRiskCalculator.calculate(
            nowAt = OffsetDateTime.now(),
            lastDepartureTime = lastDepartureTime,
            walkingMinutes = command.walkingMinutes,
        )

        return GetStationTimesDto.LastTrainRiskResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            upDownType = command.upDownType,
            walkingMinutes = command.walkingMinutes,
            nowAt = calculated.nowAt.toString(),
            lastDepartureTime = lastDepartureTime,
            minutesToLastTrain = calculated.minutesToLastTrain,
            isLastTrainRisk = calculated.isLastTrainRisk,
            riskLevel = calculated.riskLevel,
            message = calculated.message,
        )
    }

    private fun loadStationTimesForLastTrainRisk(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times last-train-risk fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    private fun loadStationTimesSafely(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times full fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    private fun resolveStrategyOrder(
        primary: SearchSubwayRouteDto.RouteSearchStrategy,
    ): List<SearchSubwayRouteDto.RouteSearchStrategy> {
        val defaults = listOf(
            SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
            SearchSubwayRouteDto.RouteSearchStrategy.MIN_TRANSFER,
            SearchSubwayRouteDto.RouteSearchStrategy.MIN_STOP,
        )
        return listOf(primary) + defaults.filterNot { it == primary }
    }

    private fun buildAdjacency(
        allLineStations: List<SubwayLineStationEntity>,
    ): Map<Long, List<GraphEdge>> {
        val adjacency = mutableMapOf<Long, MutableList<GraphEdge>>()

        allLineStations
            .groupBy { it.subwayLine.id }
            .forEach { (_, lineStations) ->
                lineStations
                    .windowed(size = 2, step = 1, partialWindows = false)
                    .forEach { pair ->
                        val left = pair[0]
                        val right = pair[1]
                        val forward = GraphEdge(
                            fromStationId = left.station.id,
                            toStationId = right.station.id,
                            subwayLineId = left.subwayLine.id,
                            subwayLineName = left.subwayLine.name,
                        )
                        val backward = GraphEdge(
                            fromStationId = right.station.id,
                            toStationId = left.station.id,
                            subwayLineId = left.subwayLine.id,
                            subwayLineName = left.subwayLine.name,
                        )
                        adjacency.getOrPut(forward.fromStationId) { mutableListOf() }.add(forward)
                        adjacency.getOrPut(backward.fromStationId) { mutableListOf() }.add(backward)
                    }
            }

        return adjacency
    }

    private fun findShortestPath(
        sourceStationId: Long,
        destinationStationId: Long,
        strategy: SearchSubwayRouteDto.RouteSearchStrategy,
        adjacency: Map<Long, List<GraphEdge>>,
        stationNamesById: Map<Long, String>,
    ): PathResult {
        if (sourceStationId == destinationStationId) {
            return PathResult(
                nodes = listOf(sourceStationId),
                edges = emptyList(),
                metric = RouteMetric(stops = 0, transfers = 0),
                stationNamesById = stationNamesById,
            )
        }

        val comparator = routeMetricComparator(strategy)
        val queue = PriorityQueue<StateWithMetric> { left, right ->
            comparator.compare(left.metric, right.metric)
        }

        val start = RouteState(sourceStationId, null)
        val distance = mutableMapOf(start to RouteMetric(stops = 0, transfers = 0))
        val previous = mutableMapOf<RouteState, Prev>()
        queue.add(StateWithMetric(start, RouteMetric(stops = 0, transfers = 0)))

        var endState: RouteState? = null
        var endMetric: RouteMetric? = null

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentBest = distance[current.state] ?: continue
            if (current.metric != currentBest) {
                continue
            }

            if (current.state.stationId == destinationStationId) {
                endState = current.state
                endMetric = current.metric
                break
            }

            adjacency[current.state.stationId].orEmpty().forEach { edge ->
                val nextState = RouteState(stationId = edge.toStationId, lineId = edge.subwayLineId)
                val isTransfer = current.state.lineId != null && current.state.lineId != edge.subwayLineId
                val nextMetric = RouteMetric(
                    stops = current.metric.stops + 1,
                    transfers = current.metric.transfers + if (isTransfer) 1 else 0,
                )
                val known = distance[nextState]
                if (known == null || comparator.compare(nextMetric, known) < 0) {
                    distance[nextState] = nextMetric
                    previous[nextState] = Prev(previousState = current.state, edge = edge)
                    queue.add(StateWithMetric(nextState, nextMetric))
                }
            }
        }

        if (endState == null || endMetric == null) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val edgesReversed = mutableListOf<GraphEdge>()
        var cursor = endState
        while (cursor != start) {
            val prev = previous[cursor] ?: break
            edgesReversed.add(prev.edge)
            cursor = prev.previousState
        }

        val edges = edgesReversed.reversed()
        val nodes = if (edges.isEmpty()) {
            listOf(sourceStationId)
        } else {
            buildList {
                add(sourceStationId)
                edges.forEach { add(it.toStationId) }
            }
        }

        if (nodes.size <= 1) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        return PathResult(
            nodes = nodes,
            edges = edges,
            metric = endMetric,
            stationNamesById = stationNamesById,
        )
    }

    private fun routeMetricComparator(
        strategy: SearchSubwayRouteDto.RouteSearchStrategy,
    ): Comparator<RouteMetric> {
        return Comparator { left, right ->
            when (strategy) {
                SearchSubwayRouteDto.RouteSearchStrategy.BALANCED -> {
                    val leftScore = estimateRouteMinutes(left.stops, left.transfers)
                    val rightScore = estimateRouteMinutes(right.stops, right.transfers)
                    if (leftScore != rightScore) {
                        leftScore.compareTo(rightScore)
                    } else if (left.transfers != right.transfers) {
                        left.transfers.compareTo(right.transfers)
                    } else {
                        left.stops.compareTo(right.stops)
                    }
                }

                SearchSubwayRouteDto.RouteSearchStrategy.MIN_TRANSFER -> {
                    if (left.transfers != right.transfers) {
                        left.transfers.compareTo(right.transfers)
                    } else {
                        left.stops.compareTo(right.stops)
                    }
                }

                SearchSubwayRouteDto.RouteSearchStrategy.MIN_STOP -> {
                    if (left.stops != right.stops) {
                        left.stops.compareTo(right.stops)
                    } else {
                        left.transfers.compareTo(right.transfers)
                    }
                }
            }
        }
    }

    private fun PathResult.toRoute(rank: Int): SearchSubwayRouteDto.Route {
        val transferStationIds = edges
            .zipWithNext()
            .filter { (left, right) -> left.subwayLineId != right.subwayLineId }
            .map { (left, _) -> left.toStationId }
            .toSet()

        return SearchSubwayRouteDto.Route(
            rank = rank,
            nodes = nodes.mapIndexed { index, stationId ->
                SearchSubwayRouteDto.Node(
                    stationId = stationId,
                    stationName = stationNamesById[stationId] ?: "알 수 없음",
                    order = index,
                    isTransfer = stationId in transferStationIds,
                )
            },
            edges = edges.map {
                SearchSubwayRouteDto.Edge(
                    fromStationId = it.fromStationId,
                    toStationId = it.toStationId,
                    subwayLineId = it.subwayLineId,
                    subwayLineName = it.subwayLineName,
                )
            },
            summary = SearchSubwayRouteDto.Summary(
                totalStops = metric.stops,
                transferCount = metric.transfers,
                estimatedMinutes = estimateRouteMinutes(metric.stops, metric.transfers),
            ),
        )
    }

    private fun estimateRouteMinutes(totalStops: Int, transferCount: Int): Int {
        return totalStops * 2 + transferCount * 4
    }

    override fun getQuickExits(command: GetStationQuickExitCommand): GetStationTimesDto.QuickExitResponse {
        val recommendations = StationQuickExitRecommendationCalculator.recommend(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
        )

        return GetStationTimesDto.QuickExitResponse(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            recommendations = recommendations,
        )
    }

    override fun searchSubwayRoutes(command: SearchSubwayRouteCommand): SearchSubwayRouteDto.Response {
        val allLineStations = subwayLineStationReader.findAllOrderedForGraph()
        if (allLineStations.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val stationNamesById = allLineStations.associate { it.station.id to it.station.name }
        if (!stationNamesById.containsKey(command.sourceStationId) ||
            !stationNamesById.containsKey(command.destinationStationId)
        ) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val adjacency = buildAdjacency(allLineStations)
        val strategyOrder = resolveStrategyOrder(command.strategy)
        val routes = mutableListOf<SearchSubwayRouteDto.Route>()
        val routeSignatures = mutableSetOf<String>()

        strategyOrder.forEach { strategy ->
            if (routes.size >= command.alternatives) {
                return@forEach
            }

            runCatching {
                findShortestPath(
                    sourceStationId = command.sourceStationId,
                    destinationStationId = command.destinationStationId,
                    strategy = strategy,
                    adjacency = adjacency,
                    stationNamesById = stationNamesById,
                )
            }.onSuccess { path ->
                val signature = path.nodes.joinToString("-")
                if (routeSignatures.add(signature)) {
                    routes.add(path.toRoute(rank = routes.size + 1))
                }
            }
        }

        if (routes.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        return SearchSubwayRouteDto.Response(
            generatedAt = OffsetDateTime.now().toString(),
            sourceStationId = command.sourceStationId,
            destinationStationId = command.destinationStationId,
            strategy = command.strategy,
            routes = routes,
        )
    }

    private fun loadStationTimes(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

        val cacheCommand = command.toCacheCommand(stationCode)
        stationTimesCacheUtils.getStationTimesByCache(cacheCommand)?.let {
            return it
        }

        val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
        if (response.isFail()) {
            throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
        }

        val stationTimes = response.toStationTimes()
        stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)
        return stationTimes
    }

    /**
     * Redis 통신 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.Response {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e : CallNotPermittedException
    ): GetStationTimesDto.Response {
        logger.error("circuit breaker opened for external station times api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e : CallNotPermittedException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("circuit breaker opened for external station times summary api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e : CallNotPermittedException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("circuit breaker opened for external station times last-train-risk api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }
}
