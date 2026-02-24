package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*

@RestController
class MemberController(
    private val memberUseCase: MemberUseCase
) {

    @Authentication
    @GetMapping("/v1/members")
    fun getMember(): CommonResponse<GetMemberDto.Response> {
        return CommonResponse.success(memberUseCase.getMember())
    }

    @Authentication
    @GetMapping("/v1/members/visibility")
    fun getMemberVisibility(): CommonResponse<MemberVisibilityDto.Response> {
        return CommonResponse.success(memberUseCase.getMemberVisibility())
    }

    @Authentication
    @PatchMapping("/v1/members/visibility")
    fun updateMemberVisibility(
        @RequestBody request: MemberVisibilityDto.Request
    ): CommonResponse<MemberVisibilityDto.Response> {
        return CommonResponse.success(memberUseCase.updateMemberVisibility(request.toCommand()))
    }

    @Authentication
    @PatchMapping("/v1/members")
    fun updateMember(
        @RequestBody request: UpdateMemberDto.Request
    ): CommonResponse<UpdateMemberDto.Response> {
        return CommonResponse.success(memberUseCase.updateMember(request.toCommand()))
    }

    @Authentication
    @DeleteMapping("/v1/members")
    fun deleteMember(@RequestHeader("Authorization") authorizationHeader: String): CommonResponse<*> {
        memberUseCase.deleteMember(DeleteMemberDto.Request(authorizationHeader))
        return CommonResponse.success()
    }

    @PostMapping("/v1/members/check-nickname")
    fun checkNickname(
        @RequestBody request: CheckNicknameDto.Request
    ): CommonResponse<CheckNicknameDto.Response> {
        return CommonResponse.success(memberUseCase.checkNickname(request.toCommand()))
    }

    @Authentication
    @PostMapping("/v1/members/bookmarks/stations")
    fun bookmarkStation(
        @RequestBody request: BookmarkStationDto.Request
    ): CommonResponse<GetBookmarkStationDto.Response> {
        return CommonResponse.success(memberUseCase.bookmarkStation(request.toCommand()))
    }

    @Authentication
    @GetMapping("/v1/members/bookmarks/stations")
    fun getBookmarkStation(): CommonResponse<GetBookmarkStationDto.Response> {
        return CommonResponse.success(memberUseCase.getBookmarkStation())
    }

    @Authentication
    @GetMapping("/v1/members/article-histories")
    fun getArticleHistories(
        @RequestParam(required = false, defaultValue = "30") limit: Int
    ): CommonResponse<GetArticleHistoryDto.Response> {
        return CommonResponse.success(memberUseCase.getArticleHistories(limit))
    }

    @GetMapping("/v1/members/search")
    fun searchMembers(
        request: SearchMemberDto.Request
    ): CommonResponse<SearchMemberDto.Response> {
        return CommonResponse.success(memberUseCase.searchMembers(request.toCommand()))
    }

    @Authentication(required = false)
    @GetMapping("/v1/members/{nickname}/profile")
    fun getMemberProfile(
        @PathVariable nickname: String,
        @RequestParam(required = false, defaultValue = "false") asPublic: Boolean,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
    ): CommonResponse<GetMemberProfileDto.Response> {
        return CommonResponse.success(memberUseCase.getMemberProfile(nickname, asPublic, limit))
    }

    @Authentication
    @PatchMapping("/v1/members/fcm-token")
    fun updateToken(
        @RequestBody request: UpdateFcmTokenDto.Request
    ): CommonResponse<*> {
        memberUseCase.updateFcmToken(request.fcmToken)
        return CommonResponse.success()
    }
}
