package backend.team.ahachul_backend.api.foreigner.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.foreigner.adapter.`in`.dto.ForeignerModeDto
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.ForeignerModeUseCase
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.ForeignerLocale
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.GetForeignerStationGuideCommand
import backend.team.ahachul_backend.api.foreigner.application.port.`in`.dto.TranslateCommunityPostCommand
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.Locale

@Service
@Transactional(readOnly = true)
class ForeignerModeService(
    private val subwayLineStationReader: SubwayLineStationReader,
    private val communityPostReader: CommunityPostReader,
    private val objectMapper: ObjectMapper,
) : ForeignerModeUseCase {

    private data class TranslationResult(
        val text: String,
        val fallback: Boolean,
    )

    override fun getStationGuide(command: GetForeignerStationGuideCommand): ForeignerModeDto.StationGuideResponse {
        val subwayLineStation =
            subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationNameKo = subwayLineStation.station.name
        val subwayLineNameKo = subwayLineStation.subwayLine.name
        val romanizedName = toTitleCaseRomanized(romanizeText(stationNameKo))

        val locale = command.locale
        val localizedStationName = localizeStationName(stationNameKo, romanizedName, locale)
        val localizedSubwayLineName = localizeSubwayLineName(subwayLineNameKo, locale)
        val templates = buildTemplates(locale, localizedStationName, localizedSubwayLineName)

        return ForeignerModeDto.StationGuideResponse(
            generatedAt = OffsetDateTime.now().toString(),
            station = ForeignerModeDto.StationDescriptor(
                stationId = subwayLineStation.station.id,
                subwayLineId = subwayLineStation.subwayLine.id,
                nameKo = stationNameKo,
                nameLocalized = localizedStationName,
                romanizedName = romanizedName,
                pronunciation = toPronunciation(stationNameKo),
                subwayLineNameKo = subwayLineNameKo,
                subwayLineNameLocalized = localizedSubwayLineName,
                locale = locale.code,
            ),
            templates = templates,
            cultureGuide = buildCultureGuide(locale),
            supportedLocales = ForeignerLocale.values().map { it.code },
        )
    }

    override fun translateCommunityPost(command: TranslateCommunityPostCommand): ForeignerModeDto.CommunityPostTranslationResponse {
        val post = communityPostReader.getByCustom(command.postId, null)
        if (post.status == CommunityPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val originalTitle = normalizeWhitespace(post.title)
        val originalContent = normalizeWhitespace(extractLexicalText(post.content))
        val titleTranslation = translateText(originalTitle, command.targetLocale)
        val contentTranslation = translateText(originalContent, command.targetLocale)
        val isFallback = titleTranslation.fallback || contentTranslation.fallback

        return ForeignerModeDto.CommunityPostTranslationResponse(
            postId = post.id,
            sourceLocale = detectSourceLocale("$originalTitle $originalContent"),
            targetLocale = command.targetLocale.code,
            originalTitle = originalTitle,
            originalContent = originalContent,
            translatedTitle = titleTranslation.text,
            translatedContent = contentTranslation.text,
            isFallback = isFallback,
            notice = buildTranslationNotice(command.targetLocale),
        )
    }

    private fun buildTemplates(
        locale: ForeignerLocale,
        stationName: String,
        lineName: String,
    ): ForeignerModeDto.TemplateBundle {
        return when (locale) {
            ForeignerLocale.KO -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] $stationName 이용 민원 접수",
                complaintBodyTemplate = """
                    안녕하세요. 아래 내용으로 민원을 접수합니다.
                    - 발생 시각:
                    - 위치(역/출구/승강장): $stationName
                    - 노선: $lineName
                    - 상세 내용:
                    - 요청 사항:
                """.trimIndent(),
                lostTitleTemplate = "[$stationName] 분실물 확인 요청",
                lostBodyTemplate = """
                    안녕하세요. 아래 분실물을 찾고 있습니다.
                    - 분실 시각:
                    - 분실 위치: $stationName
                    - 노선: $lineName
                    - 물품 정보(색상/브랜드/특징):
                    - 연락 방법:
                """.trimIndent(),
            )

            ForeignerLocale.EN -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] Service issue at $stationName",
                complaintBodyTemplate = """
                    Hello, I would like to report an issue.
                    - Date and time:
                    - Location (station/exit/platform): $stationName
                    - Line: $lineName
                    - Details:
                    - Requested action:
                """.trimIndent(),
                lostTitleTemplate = "Lost item report at $stationName",
                lostBodyTemplate = """
                    Hello, I am looking for a lost item.
                    - Lost time:
                    - Lost location: $stationName
                    - Line: $lineName
                    - Item details (color/brand/features):
                    - Contact:
                """.trimIndent(),
            )

            ForeignerLocale.TH -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] แจ้งปัญหาที่ $stationName",
                complaintBodyTemplate = """
                    สวัสดีค่ะ/ครับ ต้องการแจ้งปัญหาการใช้งาน
                    - วันที่และเวลา:
                    - จุดที่เกิดเหตุ (สถานี/ทางออก/ชานชาลา): $stationName
                    - สายรถไฟ: $lineName
                    - รายละเอียด:
                    - สิ่งที่ต้องการให้ดำเนินการ:
                """.trimIndent(),
                lostTitleTemplate = "แจ้งของหายที่ $stationName",
                lostBodyTemplate = """
                    สวัสดีค่ะ/ครับ ต้องการแจ้งของหาย
                    - เวลาที่ของหาย:
                    - สถานที่: $stationName
                    - สายรถไฟ: $lineName
                    - รายละเอียดของสิ่งของ:
                    - ช่องทางติดต่อ:
                """.trimIndent(),
            )

            ForeignerLocale.CN -> ForeignerModeDto.TemplateBundle(
                complaintTitleTemplate = "[$lineName] $stationName 使用问题反馈",
                complaintBodyTemplate = """
                    您好，我想反馈以下问题。
                    - 发生时间：
                    - 发生位置（车站/出口/站台）：$stationName
                    - 线路：$lineName
                    - 详细说明：
                    - 希望处理方式：
                """.trimIndent(),
                lostTitleTemplate = "$stationName 失物查询",
                lostBodyTemplate = """
                    您好，我想寻找遗失物品。
                    - 遗失时间：
                    - 遗失地点：$stationName
                    - 线路：$lineName
                    - 物品特征（颜色/品牌/特征）：
                    - 联系方式：
                """.trimIndent(),
            )
        }
    }

    private fun buildCultureGuide(locale: ForeignerLocale): ForeignerModeDto.CultureGuide {
        return when (locale) {
            ForeignerLocale.KO -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "막차 15분 전에는 승강장 도착을 권장합니다.",
                transferEtiquetteTip = "환승 통로에서는 한 줄 이동, 하차 승객 우선 탑승을 지켜주세요.",
                safetyTip = "혼잡 시 승강장 안전선 안쪽에서 대기하고, 무리한 탑승은 피하세요.",
                emergencyPhrase = "긴급 상황 시 역무실 또는 112에 즉시 신고하세요.",
            )

            ForeignerLocale.EN -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "Try to arrive at the platform at least 15 minutes before the last train.",
                transferEtiquetteTip = "Keep to one side in transfer corridors and let passengers exit first.",
                safetyTip = "During crowding, wait behind the safety line and avoid forcing your way in.",
                emergencyPhrase = "In emergencies, contact station staff or call 112 immediately.",
            )

            ForeignerLocale.TH -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "ควรมาถึงชานชาลาก่อนรถไฟเที่ยวสุดท้ายอย่างน้อย 15 นาที",
                transferEtiquetteTip = "เดินเรียงหนึ่งในทางเชื่อมและให้ผู้โดยสารลงก่อนขึ้น",
                safetyTip = "ช่วงคนแน่นให้ยืนหลังเส้นปลอดภัยและหลีกเลี่ยงการฝืนขึ้นรถ",
                emergencyPhrase = "กรณีฉุกเฉินให้ติดต่อเจ้าหน้าที่สถานีหรือโทร 112 ทันที",
            )

            ForeignerLocale.CN -> ForeignerModeDto.CultureGuide(
                lastTrainTip = "建议至少提前15分钟到达站台乘坐末班车。",
                transferEtiquetteTip = "换乘通道请单列通行，并先下后上。",
                safetyTip = "拥挤时请站在安全线内侧，避免强行上车。",
                emergencyPhrase = "紧急情况请立即联系车站工作人员或拨打112。",
            )
        }
    }

    private fun buildTranslationNotice(locale: ForeignerLocale): String {
        return when (locale) {
            ForeignerLocale.KO -> "자동 번역 결과는 참고용이며 원문과 차이가 있을 수 있습니다."
            ForeignerLocale.EN -> "Auto-translation is for reference and may differ from the original meaning."
            ForeignerLocale.TH -> "ผลการแปลอัตโนมัติเป็นข้อมูลอ้างอิงและอาจแตกต่างจากต้นฉบับ"
            ForeignerLocale.CN -> "自动翻译仅供参考，可能与原文含义存在差异。"
        }
    }

    private fun localizeStationName(
        stationNameKo: String,
        romanizedName: String,
        locale: ForeignerLocale,
    ): String {
        return when (locale) {
            ForeignerLocale.KO -> stationNameKo
            ForeignerLocale.EN -> "$romanizedName Station"
            ForeignerLocale.TH -> "$stationNameKo ($romanizedName)"
            ForeignerLocale.CN -> "$stationNameKo（$romanizedName）"
        }
    }

    private fun localizeSubwayLineName(
        lineNameKo: String,
        locale: ForeignerLocale,
    ): String {
        val numericLine = Regex("""(\d+)호선""").find(lineNameKo)?.groupValues?.getOrNull(1)
        if (numericLine != null) {
            return when (locale) {
                ForeignerLocale.KO -> lineNameKo
                ForeignerLocale.EN -> "Line $numericLine"
                ForeignerLocale.TH -> "สาย $numericLine"
                ForeignerLocale.CN -> "${numericLine}号线"
            }
        }

        val lineNameByLocale = mapOf(
            ForeignerLocale.EN to mapOf(
                "신분당선" to "Shinbundang Line",
                "수인분당선" to "Suin-Bundang Line",
                "경의중앙선" to "Gyeongui-Jungang Line",
                "우이신설경전철" to "Ui-Sinseol Light Rail",
                "공항철도" to "Airport Railroad",
                "김포골드라인" to "Gimpo Gold Line",
            ),
            ForeignerLocale.TH to mapOf(
                "신분당선" to "สายชินบุนดัง",
                "수인분당선" to "สายซูอิน-บุนดัง",
                "경의중앙선" to "สายคย็องอี-จุงอัง",
                "우이신설경전철" to "รถไฟรางเบาอูอี-ชินซอล",
                "공항철도" to "รถไฟสนามบิน",
                "김포골드라인" to "สายกิมโปโกลด์",
            ),
            ForeignerLocale.CN to mapOf(
                "신분당선" to "新盆唐线",
                "수인분당선" to "水仁盆唐线",
                "경의중앙선" to "京义中央线",
                "우이신설경전철" to "牛耳新设轻轨",
                "공항철도" to "机场铁路",
                "김포골드라인" to "金浦黄金线",
            ),
        )

        return when (locale) {
            ForeignerLocale.KO -> lineNameKo
            else -> lineNameByLocale[locale]?.get(lineNameKo)
                ?: "$lineNameKo (${toTitleCaseRomanized(romanizeText(lineNameKo))})"
        }
    }

    private fun detectSourceLocale(text: String): String {
        return if (text.any { isHangul(it) }) {
            ForeignerLocale.KO.code
        } else {
            ForeignerLocale.EN.code
        }
    }

    private fun extractLexicalText(raw: String): String {
        if (raw.isBlank()) {
            return ""
        }

        val trimmed = raw.trim()
        if (!trimmed.startsWith("{")) {
            return trimmed
        }

        return runCatching {
            val tree = objectMapper.readTree(trimmed)
            val collector = mutableListOf<String>()
            collectTextNodes(tree, collector)
            normalizeWhitespace(collector.joinToString(" "))
        }.getOrDefault(trimmed)
    }

    private fun collectTextNodes(node: JsonNode, collector: MutableList<String>) {
        when {
            node.isObject -> {
                val textNode = node.get("text")
                if (textNode != null && textNode.isTextual) {
                    collector.add(textNode.asText())
                }
                node.fields().forEachRemaining { (_, child) ->
                    collectTextNodes(child, collector)
                }
            }

            node.isArray -> {
                node.forEach { child ->
                    collectTextNodes(child, collector)
                }
            }
        }
    }

    private fun translateText(source: String, targetLocale: ForeignerLocale): TranslationResult {
        if (targetLocale == ForeignerLocale.KO) {
            return TranslationResult(source, fallback = false)
        }

        if (source.isBlank()) {
            return TranslationResult(
                when (targetLocale) {
                    ForeignerLocale.EN -> "No content"
                    ForeignerLocale.TH -> "ไม่มีเนื้อหา"
                    ForeignerLocale.CN -> "无内容"
                    ForeignerLocale.KO -> "내용 없음"
                },
                fallback = true,
            )
        }

        val dictionary = TRANSLATION_DICTIONARY[targetLocale].orEmpty()
        var translated = source
        var replacementCount = 0
        val linePattern = Regex("""(\d+)호선""")
        translated = linePattern.replace(translated) { matchResult ->
            replacementCount += 1
            val lineNo = matchResult.groupValues[1]
            when (targetLocale) {
                ForeignerLocale.EN -> "Line $lineNo"
                ForeignerLocale.TH -> "สาย $lineNo"
                ForeignerLocale.CN -> "${lineNo}号线"
                ForeignerLocale.KO -> matchResult.value
            }
        }

        dictionary.forEach { (origin, converted) ->
            if (translated.contains(origin)) {
                replacementCount += 1
                translated = translated.replace(origin, converted)
            }
        }

        val normalizedTranslated = normalizeWhitespace(romanizeResidualHangul(translated))
        return if (replacementCount == 0) {
            TranslationResult(normalizedTranslated, fallback = true)
        } else {
            TranslationResult(normalizedTranslated, fallback = false)
        }
    }

    private fun romanizeResidualHangul(value: String): String {
        val builder = StringBuilder()
        value.forEach { ch ->
            if (isHangul(ch)) {
                builder.append(romanizeHangulSyllable(ch))
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    private fun toPronunciation(text: String): String {
        return text.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.map { ch ->
                    if (isHangul(ch)) {
                        romanizeHangulSyllable(ch)
                    } else {
                        ch.toString()
                    }
                }.joinToString("-")
            }
    }

    private fun romanizeText(text: String): String {
        val builder = StringBuilder()
        text.forEach { ch ->
            if (isHangul(ch)) {
                builder.append(romanizeHangulSyllable(ch))
            } else {
                builder.append(ch)
            }
        }
        return normalizeWhitespace(builder.toString())
    }

    private fun toTitleCaseRomanized(value: String): String {
        return value.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { token ->
                token.replaceFirstChar { first ->
                    if (first.isLowerCase()) {
                        first.titlecase(Locale.ENGLISH)
                    } else {
                        first.toString()
                    }
                }
            }
    }

    private fun normalizeWhitespace(value: String): String {
        return value.replace(Regex("\\s+"), " ").trim()
    }

    private fun isHangul(ch: Char): Boolean = ch.code in HANGUL_BASE..HANGUL_LAST

    private fun romanizeHangulSyllable(ch: Char): String {
        if (!isHangul(ch)) {
            return ch.toString()
        }

        val syllableIndex = ch.code - HANGUL_BASE
        val choIndex = syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT)
        val jungIndex = (syllableIndex % (JUNGSEONG_COUNT * JONGSEONG_COUNT)) / JONGSEONG_COUNT
        val jongIndex = syllableIndex % JONGSEONG_COUNT

        return CHOSEONG_ROMANIZATION[choIndex] +
            JUNGSEONG_ROMANIZATION[jungIndex] +
            JONGSEONG_ROMANIZATION[jongIndex]
    }

    companion object {
        private const val HANGUL_BASE = 0xAC00
        private const val HANGUL_LAST = 0xD7A3
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        private val CHOSEONG_ROMANIZATION = arrayOf(
            "g", "kk", "n", "d", "tt", "r", "m", "b", "pp", "s", "ss", "", "j", "jj", "ch", "k", "t", "p", "h",
        )

        private val JUNGSEONG_ROMANIZATION = arrayOf(
            "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "wo", "we", "wi", "yu", "eu", "ui", "i",
        )

        private val JONGSEONG_ROMANIZATION = arrayOf(
            "", "k", "k", "ks", "n", "nj", "nh", "t", "l", "lk", "lm", "lb", "ls", "lt", "lp", "lh", "m", "p", "ps", "t", "t", "ng", "t", "t", "k", "t", "p", "h",
        )

        private val TRANSLATION_DICTIONARY: Map<ForeignerLocale, List<Pair<String, String>>> = mapOf(
            ForeignerLocale.EN to listOf(
                "지하철" to "subway",
                "열차" to "train",
                "환승" to "transfer",
                "막차" to "last train",
                "지연" to "delay",
                "혼잡" to "crowded",
                "사고" to "incident",
                "운행" to "operation",
                "출구" to "exit",
                "승강장" to "platform",
                "분실물" to "lost item",
                "민원" to "complaint",
                "빠르게" to "quickly",
                "늦습니다" to "running late",
                "도착예정" to "ETA",
            ),
            ForeignerLocale.TH to listOf(
                "지하철" to "รถไฟใต้ดิน",
                "열차" to "ขบวนรถ",
                "환승" to "เปลี่ยนสาย",
                "막차" to "รถเที่ยวสุดท้าย",
                "지연" to "ล่าช้า",
                "혼잡" to "แออัด",
                "사고" to "เหตุขัดข้อง",
                "출구" to "ทางออก",
                "승강장" to "ชานชาลา",
                "분실물" to "ของหาย",
                "민원" to "คำร้องเรียน",
            ),
            ForeignerLocale.CN to listOf(
                "지하철" to "地铁",
                "열차" to "列车",
                "환승" to "换乘",
                "막차" to "末班车",
                "지연" to "延误",
                "혼잡" to "拥挤",
                "사고" to "事故",
                "출구" to "出口",
                "승강장" to "站台",
                "분실물" to "失物",
                "민원" to "投诉",
            ),
        )
    }
}
