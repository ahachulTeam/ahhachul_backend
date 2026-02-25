package backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto

import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.CreateForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialHotspotsCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationSocialOverviewCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.JoinForeignerStationSocialMeetupCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.OpenForeignerStationSocialMatchCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ReviewForeignerStationSocialParticipantCommand
import java.time.LocalDateTime

class ForeignerStationSocialDto {

    data class HotspotsRequest(
        val locale: String? = null,
    ) {
        fun toCommand() = GetForeignerStationSocialHotspotsCommand(
            locale = ForeignerLocale.from(locale),
        )
    }

    data class OverviewRequest(
        val stationId: Long,
        val subwayLineId: Long? = null,
        val locale: String? = null,
        val sameNationalityOnly: Boolean = false,
        val nationalityCode: String? = null,
        val limit: Int? = null,
    ) {
        fun toCommand() = GetForeignerStationSocialOverviewCommand(
            stationId = stationId,
            subwayLineId = subwayLineId,
            locale = ForeignerLocale.from(locale),
            sameNationalityOnly = sameNationalityOnly,
            nationalityCode = nationalityCode?.trim()?.uppercase()?.takeIf { it.isNotBlank() },
            limit = (limit ?: 20).coerceIn(1, 100),
        )
    }

    data class CreateMeetupRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val title: String,
        val description: String,
        val meetupAt: String,
        val maxParticipants: Int,
        val nationalityCode: String? = null,
        val sameNationalityOnly: Boolean = false,
    ) {
        fun toCommand() = CreateForeignerStationSocialMeetupCommand(
            stationId = stationId,
            subwayLineId = subwayLineId,
            title = title,
            description = description,
            meetupAt = LocalDateTime.parse(meetupAt),
            maxParticipants = maxParticipants,
            nationalityCode = nationalityCode?.trim()?.uppercase()?.takeIf { it.isNotBlank() },
            sameNationalityOnly = sameNationalityOnly,
        )
    }

    data class JoinMeetupRequest(
        val introductionMessage: String? = null,
        val nationalityCode: String? = null,
    ) {
        fun toCommand(meetupId: Long) = JoinForeignerStationSocialMeetupCommand(
            meetupId = meetupId,
            introductionMessage = introductionMessage?.trim()?.takeIf { it.isNotBlank() },
            nationalityCode = nationalityCode?.trim()?.uppercase()?.takeIf { it.isNotBlank() },
        )
    }

    data class ReviewParticipantRequest(
        val approve: Boolean,
    ) {
        fun toCommand(meetupId: Long, participantId: Long) = ReviewForeignerStationSocialParticipantCommand(
            meetupId = meetupId,
            participantId = participantId,
            approve = approve,
        )
    }

    data class OpenMatchRequest(
        val targetMemberId: Long,
        val openingMessage: String? = null,
    ) {
        fun toCommand(meetupId: Long) = OpenForeignerStationSocialMatchCommand(
            meetupId = meetupId,
            targetMemberId = targetMemberId,
            openingMessage = openingMessage?.trim()?.takeIf { it.isNotBlank() },
        )
    }

    data class HotspotsResponse(
        val generatedAt: String,
        val locale: String,
        val hotspots: List<HotspotStation>,
    )

    data class HotspotStation(
        val stationId: Long,
        val subwayLineId: Long,
        val stationNameKo: String,
        val stationNameLocalized: String,
        val lineNameLocalized: String,
        val romanizedName: String,
        val districtLabel: String,
        val summary: String,
        val contentTags: List<String>,
        val upcomingMeetupCount: Long,
        val reviewCount: Int,
    )

    data class OverviewResponse(
        val generatedAt: String,
        val locale: String,
        val station: StationInfo,
        val sameNationalityOnly: Boolean,
        val nationalityCode: String?,
        val calendar: List<CalendarItem>,
        val meetups: List<MeetupItem>,
        val reviewPosts: List<ReviewPostItem>,
    )

    data class StationInfo(
        val stationId: Long,
        val subwayLineId: Long,
        val stationNameKo: String,
        val stationNameLocalized: String,
        val lineNameKo: String,
        val lineNameLocalized: String,
        val romanizedName: String,
        val pronunciation: String,
        val cultureTips: List<String>,
    )

    data class CalendarItem(
        val date: String,
        val meetupCount: Int,
    )

    data class MeetupItem(
        val meetupId: Long,
        val title: String,
        val description: String,
        val meetupAt: String,
        val maxParticipants: Int,
        val acceptedCount: Long,
        val hostMemberId: Long,
        val hostNickname: String,
        val nationalityCode: String?,
        val sameNationalityOnly: Boolean,
        val status: String,
        val mine: Boolean,
        val participants: List<ParticipantItem>,
    )

    data class ParticipantItem(
        val participantId: Long,
        val memberId: Long,
        val nickname: String,
        val nationalityCode: String?,
        val status: String,
        val mine: Boolean,
    )

    data class ReviewPostItem(
        val postId: Long,
        val title: String,
        val preview: String,
        val writer: String,
        val createdAt: String,
    )

    data class CreateMeetupResponse(
        val meetupId: Long,
        val createdAt: String,
    )

    data class JoinMeetupResponse(
        val meetupId: Long,
        val participantId: Long,
        val status: String,
    )

    data class ReviewParticipantResponse(
        val meetupId: Long,
        val participantId: Long,
        val status: String,
    )

    data class OpenMatchResponse(
        val meetupId: Long,
        val targetMemberId: Long,
        val roomId: Long,
        val messageId: Long,
    )
}
