package backend.team.ahachul_backend.common.client.impl

import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.properties.PublicDataProperties
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

class SeoulTrainClientImplTest: CommonServiceTestConfig() {

    private lateinit var mockServer: MockRestServiceServer
    private lateinit var restClient: RestClient
    private lateinit var seoulTrainClient: SeoulTrainClient

    @Autowired
    lateinit var publicDataProperties: PublicDataProperties

    @BeforeEach
    fun setUp() {
        val builder = RestClient.builder()
        mockServer = MockRestServiceServer.bindTo(builder).build()
        restClient = builder.build()
        seoulTrainClient = SeoulTrainClientImpl(restClient, publicDataProperties)
    }

    @Test
    @DisplayName("역 시간표 API 조회 시 실패")
    fun getStationTimesByApiWithFail() {
        //given
        val request = StationTimesDto.Request(
            stationCd = "0339",
            weekTag = StationTimeWeekType.WEEKDAY.publicCode,
            inoutTag = UpDownType.DOWN.publicCode
        )

        val failResponse = """
            {
              "RESULT": {
                "CODE": "INFO-200",
                "MESSAGE": "해당하는 데이터가 없습니다."
              }
            }
        """.trimIndent()

        val url = "${publicDataProperties.stationTimesPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}${publicDataProperties.stationTimesSuffixUri}/" +
            "${request.startIndex}/${request.endIndex}/${request.stationCd}/${request.weekTag}/${request.inoutTag}"

        mockServer.expect(requestTo(url))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(failResponse, MediaType.APPLICATION_JSON)
            )

        //when & then
        assertThatThrownBy { seoulTrainClient.getStationTimesByApi(request) }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.FAILED_STATION_TIMES_API.message)
    }

    @Test
    @DisplayName("역 시간표 API 조회 성공")
    fun getStationTimesByApiWithSuccess() {
        //given
        val request = StationTimesDto.Request(
            stationCd = "0339",
            weekTag = StationTimeWeekType.WEEKDAY.publicCode,
            inoutTag = UpDownType.DOWN.publicCode
        )

        val failResponse = """
            {
              "SearchSTNTimeTableByIDService": {
                "list_total_count": 191,
                "RESULT": {
                  "CODE": "INFO-000",
                  "MESSAGE": "정상 처리되었습니다"
                },
                "row": [
                  {
                    "LINE_NUM": "03호선", 
                    "FR_CODE": "349", 
                    "STATION_CD": "0339", 
                    "STATION_NM": "수서", 
                    "TRAIN_NO": "3018", 
                    "ARRIVETIME": "00:00:00", 
                    "LEFTTIME": "05:22:00", 
                    "ORIGINSTATION": "0339", 
                    "DESTSTATION": "0310", 
                    "SUBWAYSNAME": "수서", 
                    "SUBWAYENAME": "구파발", 
                    "WEEK_TAG": "1", 
                    "INOUT_TAG": "1",
                    "FL_FLAG": "", 
                    "DESTSTATION2": "", 
                    "EXPRESS_YN": "G", 
                    "BRANCH_LINE": "" 
                  },
                  {
                    "LINE_NUM": "03호선",
                    "FR_CODE": "349",
                    "STATION_CD": "0339",
                    "STATION_NM": "수서",
                    "TRAIN_NO": "3022",
                    "ARRIVETIME": "00:00:00",
                    "LEFTTIME": "05:30:00",
                    "ORIGINSTATION": "0339",
                    "DESTSTATION": "1958",
                    "SUBWAYSNAME": "수서",
                    "SUBWAYENAME": "대화",
                    "WEEK_TAG": "1",
                    "INOUT_TAG": "1",
                    "FL_FLAG": "",
                    "DESTSTATION2": "",
                    "EXPRESS_YN": "G",
                    "BRANCH_LINE": ""
                  }
                ]
              }
            }
        """.trimIndent()

        val url =
            "${publicDataProperties.stationTimesPrefixUri}/${publicDataProperties.realTimeStationArrivalToken}${publicDataProperties.stationTimesSuffixUri}/" +
                "${request.startIndex}/${request.endIndex}/${request.stationCd}/${request.weekTag}/${request.inoutTag}"

        mockServer.expect(requestTo(url))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(failResponse, MediaType.APPLICATION_JSON)
            )

        //when
        val response = seoulTrainClient.getStationTimesByApi(request)

        //then
        assertThat(response.isFail()).isFalse()
        assertThat(response.stationTimesTable.totalCount).isEqualTo(191)
        assertThat(response.stationTimesTable.result.code).isEqualTo("INFO-000")
        assertThat(response.stationTimesTable.rows.size).isEqualTo(2)
        assertThat(response.stationTimesTable.rows[0].stationNm).isEqualTo("수서")
    }

}