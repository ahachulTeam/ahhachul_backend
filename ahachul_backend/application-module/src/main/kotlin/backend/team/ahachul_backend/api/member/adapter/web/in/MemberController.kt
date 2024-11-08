package backend.team.ahachul_backend.api.member.adapter.web.`in`

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
    @PatchMapping("/v1/members")
    fun updateMember(
            @RequestBody request: UpdateMemberDto.Request
    ): CommonResponse<UpdateMemberDto.Response> {
        return CommonResponse.success(memberUseCase.updateMember(request.toCommand()))
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
    ): CommonResponse<BookmarkStationDto.Response> {
        return CommonResponse.success(memberUseCase.bookmarkStation(request.toCommand()))
    }

    @Authentication
    @GetMapping("/v1/members/bookmarks/stations")
    fun getBookmarkStation(): CommonResponse<GetBookmarkStationDto.Response> {
        return CommonResponse.success(memberUseCase.getBookmarkStation())
    }
}
