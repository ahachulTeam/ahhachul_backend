package backend.team.ahachul_backend.api.foreigner.adapter.`in`

import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ForeignerModeController::class)
class ForeignerModeControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var foreignerModeUseCase: ForeignerModeUseCase

    @Test
    fun getStationGuide() {
        val response = ForeignerModeDto.StationGuideResponse(
            generatedAt = "2026-02-25T23:10:00+09:00",
            station = ForeignerModeDto.StationDescriptor(
                stationId = 557,
                subwayLineId = 18,
                nameKo = "안암",
                nameLocalized = "Anam Station",
                romanizedName = "Anam",
                pronunciation = "a-nam",
                subwayLineNameKo = "우이신설경전철",
                subwayLineNameLocalized = "Ui-Sinseol Light Rail",
                locale = "en",
            ),
            templates = ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[Ui-Sinseol Light Rail] Service issue at Anam Station",
                complaintBodyTemplate = "Hello, I would like to report an issue.",
                lostTitleTemplate = "Lost item report at Anam Station",
                lostBodyTemplate = "Hello, I am looking for a lost item.",
            ),
            cultureGuide = ForeignerModeDto.CultureGuide(
                lastTrainTip = "Try to arrive at the platform at least 15 minutes before the last train.",
                transferEtiquetteTip = "Keep to one side in transfer corridors and let passengers exit first.",
                safetyTip = "During crowding, wait behind the safety line and avoid forcing your way in.",
                emergencyPhrase = "In emergencies, contact station staff or call 112 immediately.",
            ),
            supportedLocales = listOf("ko", "en", "th", "cn"),
        )

        given(foreignerModeUseCase.getStationGuide(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/stations/guide")
                .queryParam("stationId", "557")
                .queryParam("subwayLineId", "18")
                .queryParam("locale", "en")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-station-guide",
                    getDocsRequest(),
                    getDocsResponse(),
                    queryParameters(
                        parameterWithName("stationId").description("역 ID"),
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("locale").optional().description("언어 코드(ko/en/th/cn), 미입력 시 en")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.generatedAt").type(JsonFieldType.STRING).description("응답 생성 시각"),
                        fieldWithPath("result.station.stationId").type(JsonFieldType.NUMBER).description("역 ID"),
                        fieldWithPath("result.station.subwayLineId").type(JsonFieldType.NUMBER).description("노선 ID"),
                        fieldWithPath("result.station.nameKo").type(JsonFieldType.STRING).description("역명(한국어)"),
                        fieldWithPath("result.station.nameLocalized").type(JsonFieldType.STRING).description("역명(선택 locale)"),
                        fieldWithPath("result.station.romanizedName").type(JsonFieldType.STRING).description("역명 로마자"),
                        fieldWithPath("result.station.pronunciation").type(JsonFieldType.STRING).description("발음 가이드"),
                        fieldWithPath("result.station.subwayLineNameKo").type(JsonFieldType.STRING).description("노선명(한국어)"),
                        fieldWithPath("result.station.subwayLineNameLocalized").type(JsonFieldType.STRING).description("노선명(선택 locale)"),
                        fieldWithPath("result.station.locale").type(JsonFieldType.STRING).description("반영 locale"),
                        fieldWithPath("result.templates.complaintTitleTemplate").type(JsonFieldType.STRING).description("민원 제목 템플릿"),
                        fieldWithPath("result.templates.complaintBodyTemplate").type(JsonFieldType.STRING).description("민원 본문 템플릿"),
                        fieldWithPath("result.templates.lostTitleTemplate").type(JsonFieldType.STRING).description("유실물 제목 템플릿"),
                        fieldWithPath("result.templates.lostBodyTemplate").type(JsonFieldType.STRING).description("유실물 본문 템플릿"),
                        fieldWithPath("result.cultureGuide.lastTrainTip").type(JsonFieldType.STRING).description("막차 안내"),
                        fieldWithPath("result.cultureGuide.transferEtiquetteTip").type(JsonFieldType.STRING).description("환승 예절 안내"),
                        fieldWithPath("result.cultureGuide.safetyTip").type(JsonFieldType.STRING).description("안전 안내"),
                        fieldWithPath("result.cultureGuide.emergencyPhrase").type(JsonFieldType.STRING).description("긴급 문구"),
                        fieldWithPath("result.supportedLocales").type(JsonFieldType.ARRAY).description("지원 locale 목록"),
                    )
                )
            )
    }

    @Test
    fun translateCommunityPost() {
        val response = ForeignerModeDto.CommunityPostTranslationResponse(
            postId = 101,
            sourceLocale = "ko",
            targetLocale = "en",
            originalTitle = "2호선 지연 안내",
            originalContent = "지하철이 10분 이상 지연되고 있습니다.",
            translatedTitle = "Line 2 delay notice",
            translatedContent = "The subway is delayed for more than 10 minutes.",
            isFallback = false,
            notice = "Auto-translation is for reference and may differ from the original meaning.",
        )

        given(foreignerModeUseCase.translateCommunityPost(any())).willReturn(response)

        val result = mockMvc.perform(
            get("/v2/foreigner/community-posts/{postId}/translation", 101)
                .queryParam("targetLocale", "en")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().isOk)
            .andDo(
                document(
                    "get-foreigner-community-post-translation",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("postId").description("커뮤니티 게시글 ID")
                    ),
                    queryParameters(
                        parameterWithName("targetLocale").optional().description("번역 대상 locale(ko/en/th/cn), 미입력 시 en")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("result.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("result.sourceLocale").type(JsonFieldType.STRING).description("원문 언어 추정"),
                        fieldWithPath("result.targetLocale").type(JsonFieldType.STRING).description("번역 대상 언어"),
                        fieldWithPath("result.originalTitle").type(JsonFieldType.STRING).description("원문 제목"),
                        fieldWithPath("result.originalContent").type(JsonFieldType.STRING).description("원문 본문"),
                        fieldWithPath("result.translatedTitle").type(JsonFieldType.STRING).description("번역 제목"),
                        fieldWithPath("result.translatedContent").type(JsonFieldType.STRING).description("번역 본문"),
                        fieldWithPath("result.isFallback").type(JsonFieldType.BOOLEAN).description("fallback 번역 여부"),
                        fieldWithPath("result.notice").type(JsonFieldType.STRING).description("번역 안내 문구"),
                    )
                )
            )
    }
}
