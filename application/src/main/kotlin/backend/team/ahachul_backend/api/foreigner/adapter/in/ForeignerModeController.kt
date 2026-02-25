package backend.team.ahachul_backend.api.foreigner.adapter.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerStationSocialDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ForeignerModeController(
    private val foreignerModeUseCase: ForeignerModeUseCase,
) {

    @GetMapping("/v2/foreigner/stations/guide")
    fun getStationGuide(
        request: ForeignerModeDto.StationGuideRequest,
    ): CommonResponse<ForeignerModeDto.StationGuideResponse> {
        val result = foreignerModeUseCase.getStationGuide(request.toCommand())
        return CommonResponse.success(result)
    }

    @Authentication(required = false)
    @GetMapping("/v2/foreigner/community-posts/{postId}/translation")
    fun translateCommunityPost(
        @PathVariable postId: Long,
        request: ForeignerModeDto.CommunityPostTranslationRequest,
    ): CommonResponse<ForeignerModeDto.CommunityPostTranslationResponse> {
        val result = foreignerModeUseCase.translateCommunityPost(request.toCommand(postId))
        return CommonResponse.success(result)
    }

    @Authentication(required = false)
    @GetMapping("/v2/foreigner/station-social/hotspots")
    fun getStationSocialHotspots(
        request: ForeignerStationSocialDto.HotspotsRequest,
    ): CommonResponse<ForeignerStationSocialDto.HotspotsResponse> {
        return CommonResponse.success(foreignerModeUseCase.getStationSocialHotspots(request.toCommand()))
    }

    @Authentication(required = false)
    @GetMapping("/v2/foreigner/station-social/overview")
    fun getStationSocialOverview(
        request: ForeignerStationSocialDto.OverviewRequest,
    ): CommonResponse<ForeignerStationSocialDto.OverviewResponse> {
        return CommonResponse.success(foreignerModeUseCase.getStationSocialOverview(request.toCommand()))
    }

    @PostMapping("/v2/foreigner/station-social/meetups")
    fun createStationSocialMeetup(
        @RequestBody request: ForeignerStationSocialDto.CreateMeetupRequest,
    ): CommonResponse<ForeignerStationSocialDto.CreateMeetupResponse> {
        return CommonResponse.success(foreignerModeUseCase.createStationSocialMeetup(request.toCommand()))
    }

    @PostMapping("/v2/foreigner/station-social/meetups/{meetupId}/join")
    fun joinStationSocialMeetup(
        @PathVariable meetupId: Long,
        @RequestBody request: ForeignerStationSocialDto.JoinMeetupRequest,
    ): CommonResponse<ForeignerStationSocialDto.JoinMeetupResponse> {
        return CommonResponse.success(foreignerModeUseCase.joinStationSocialMeetup(request.toCommand(meetupId)))
    }

    @PatchMapping("/v2/foreigner/station-social/meetups/{meetupId}/participants/{participantId}")
    fun reviewStationSocialParticipant(
        @PathVariable meetupId: Long,
        @PathVariable participantId: Long,
        @RequestBody request: ForeignerStationSocialDto.ReviewParticipantRequest,
    ): CommonResponse<ForeignerStationSocialDto.ReviewParticipantResponse> {
        return CommonResponse.success(
            foreignerModeUseCase.reviewStationSocialParticipant(request.toCommand(meetupId, participantId)),
        )
    }

    @PostMapping("/v2/foreigner/station-social/meetups/{meetupId}/match")
    fun openStationSocialMatch(
        @PathVariable meetupId: Long,
        @RequestBody request: ForeignerStationSocialDto.OpenMatchRequest,
    ): CommonResponse<ForeignerStationSocialDto.OpenMatchResponse> {
        return CommonResponse.success(foreignerModeUseCase.openStationSocialMatch(request.toCommand(meetupId)))
    }
}
