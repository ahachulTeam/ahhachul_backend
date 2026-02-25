package backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto

import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand

class ForeignerModeDto {

    data class StationGuideRequest(
        val stationId: Long,
        val subwayLineId: Long,
        val locale: String? = null,
    ) {
        fun toCommand() = GetForeignerStationGuideCommand(
            stationId = stationId,
            subwayLineId = subwayLineId,
            locale = ForeignerLocale.from(locale),
        )
    }

    data class CommunityPostTranslationRequest(
        val targetLocale: String? = null,
    ) {
        fun toCommand(postId: Long) = TranslateCommunityPostCommand(
            postId = postId,
            targetLocale = ForeignerLocale.from(targetLocale),
        )
    }

    data class StationGuideResponse(
        val generatedAt: String,
        val station: StationDescriptor,
        val templates: TemplateBundle,
        val cultureGuide: CultureGuide,
        val oneClickActions: List<OneClickAction>,
        val supportedLocales: List<String>,
    )

    data class StationDescriptor(
        val stationId: Long,
        val subwayLineId: Long,
        val nameKo: String,
        val nameLocalized: String,
        val romanizedName: String,
        val pronunciation: String,
        val subwayLineNameKo: String,
        val subwayLineNameLocalized: String,
        val locale: String,
    )

    data class TemplateBundle(
        val complaintTitleTemplate: String,
        val complaintBodyTemplate: String,
        val lostTitleTemplate: String,
        val lostBodyTemplate: String,
    )

    data class CultureGuide(
        val lastTrainTip: String,
        val transferEtiquetteTip: String,
        val safetyTip: String,
        val emergencyPhrase: String,
    )

    data class OneClickAction(
        val actionType: OneClickActionType,
        val title: String,
        val description: String,
        val deepLink: String,
        val payloadTemplate: String?,
    )

    enum class OneClickActionType {
        CALL_EMERGENCY_112,
        OPEN_LOST_REPORT,
        OPEN_COMPLAINT_REPORT,
        COPY_EMERGENCY_PHRASE,
    }

    data class CommunityPostTranslationResponse(
        val postId: Long,
        val sourceLocale: String,
        val targetLocale: String,
        val originalTitle: String,
        val originalContent: String,
        val translatedTitle: String,
        val translatedContent: String,
        val isFallback: Boolean,
        val notice: String,
    )
}
