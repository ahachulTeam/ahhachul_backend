package backend.team.ahachul_backend.api.foreigner.application.port.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerStationSocialDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.CreateForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialHotspotsCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialOverviewCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.JoinForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.OpenForeignerStationSocialMatchCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ReviewForeignerStationSocialParticipantCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand

interface ForeignerModeUseCase {

    fun getStationGuide(command: GetForeignerStationGuideCommand): ForeignerModeDto.StationGuideResponse

    fun translateCommunityPost(command: TranslateCommunityPostCommand): ForeignerModeDto.CommunityPostTranslationResponse

    fun getStationSocialHotspots(command: GetForeignerStationSocialHotspotsCommand): ForeignerStationSocialDto.HotspotsResponse

    fun getStationSocialOverview(command: GetForeignerStationSocialOverviewCommand): ForeignerStationSocialDto.OverviewResponse

    fun createStationSocialMeetup(command: CreateForeignerStationSocialMeetupCommand): ForeignerStationSocialDto.CreateMeetupResponse

    fun joinStationSocialMeetup(command: JoinForeignerStationSocialMeetupCommand): ForeignerStationSocialDto.JoinMeetupResponse

    fun reviewStationSocialParticipant(command: ReviewForeignerStationSocialParticipantCommand): ForeignerStationSocialDto.ReviewParticipantResponse

    fun openStationSocialMatch(command: OpenForeignerStationSocialMatchCommand): ForeignerStationSocialDto.OpenMatchResponse
}
