package backend.team.ahachul_backend.api.member.application.port.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.command.BookmarkStationCommands
import backend.team.ahachul_backend.api.member.application.command.CreateFavoriteRouteCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.CheckNicknameCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand

interface MemberUseCase {

    fun getMember(): GetMemberDto.Response

    fun getMemberProfile(nickname: String, asPublic: Boolean, limit: Int): GetMemberProfileDto.Response

    fun updateMember(command: UpdateMemberCommand): UpdateMemberDto.Response

    fun deleteMember(request: DeleteMemberDto.Request)

    fun checkNickname(command: CheckNicknameCommand): CheckNicknameDto.Response

    fun bookmarkStation(command: BookmarkStationCommands): GetBookmarkStationDto.Response

    fun getBookmarkStation(): GetBookmarkStationDto.Response

    fun getFavoriteRouteRecommendations(limit: Int): FavoriteRouteDto.GraphResponse

    fun getFavoriteRoutes(): FavoriteRouteDto.GraphResponse

    fun createFavoriteRoute(command: CreateFavoriteRouteCommand): FavoriteRouteDto.Route

    fun deleteFavoriteRoute(routeId: Long): FavoriteRouteDto.DeleteResponse

    fun getArticleHistories(limit: Int): GetArticleHistoryDto.Response

    fun searchMembers(command: SearchMemberCommand): SearchMemberDto.Response
    
    fun updateFcmToken(fcmToken: String)
}
