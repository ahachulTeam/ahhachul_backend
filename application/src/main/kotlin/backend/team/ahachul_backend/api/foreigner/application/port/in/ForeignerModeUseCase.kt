package backend.team.ahachul_backend.api.foreigner.application.port.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand

interface ForeignerModeUseCase {

    fun getStationGuide(command: GetForeignerStationGuideCommand): ForeignerModeDto.StationGuideResponse

    fun translateCommunityPost(command: TranslateCommunityPostCommand): ForeignerModeDto.CommunityPostTranslationResponse
}
