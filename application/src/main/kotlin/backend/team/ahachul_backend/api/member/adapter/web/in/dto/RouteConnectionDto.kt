package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

class RouteConnectionDto {

    data class Response(
        val generatedAt: String,
        val matchingPolicy: MatchingPolicy,
        val anchorRoute: AnchorRoute?,
        val recommendations: List<MemberRecommendation>,
        val groups: List<RouteGroup>,
        val graph: SocialGraph,
    )

    data class MatchingPolicy(
        val sourceMaxDistance: Int,
        val destinationMaxDistance: Int,
        val totalMaxDistance: Int,
    )

    data class AnchorRoute(
        val sourceStationId: Long,
        val sourceStationName: String,
        val destinationStationId: Long,
        val destinationStationName: String,
    )

    data class MemberRecommendation(
        val memberId: Long,
        val nickname: String,
        val routeId: Long?,
        val title: String?,
        val sourceStationId: Long,
        val sourceStationName: String,
        val destinationStationId: Long,
        val destinationStationName: String,
        val sourceDistance: Int,
        val destinationDistance: Int,
        val totalDistance: Int,
        val matchScore: Int,
        val estimatedMinutes: Int,
        val reason: String,
    )

    data class RouteGroup(
        val groupId: String,
        val sourceStationId: Long,
        val sourceStationName: String,
        val destinationStationId: Long,
        val destinationStationName: String,
        val memberCount: Int,
        val members: List<RouteGroupMember>,
    )

    data class RouteGroupMember(
        val memberId: Long,
        val nickname: String,
        val matchScore: Int,
        val totalDistance: Int,
    )

    data class SocialGraph(
        val nodes: List<SocialGraphNode>,
        val edges: List<SocialGraphEdge>,
    )

    data class SocialGraphNode(
        val memberId: Long,
        val nickname: String,
        val me: Boolean,
    )

    data class SocialGraphEdge(
        val fromMemberId: Long,
        val toMemberId: Long,
        val score: Int,
        val label: String,
    )
}
