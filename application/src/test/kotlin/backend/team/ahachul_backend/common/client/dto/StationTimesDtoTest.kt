package backend.team.ahachul_backend.common.client.dto

import backend.team.ahachul_backend.common.client.dto.StationTimesDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class StationTimesDtoTest {

    var objectMapper: ObjectMapper = ObjectMapper()

    @Test
    @DisplayName("역 시간표 API 응답 객체를 파싱한다.")
    fun parseStationTimesApiResponse() {
        //given
        val apiResponseJson = """
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

        //when
        val parsedJson = objectMapper.readValue(apiResponseJson, StationTimesDto.Response::class.java)

        //then
        assertThat(parsedJson.stationTimesTable.totalCount).isEqualTo(191)
        assertThat(parsedJson.stationTimesTable.result.code).isEqualTo("INFO-000")
        assertThat(parsedJson.stationTimesTable.rows.size).isEqualTo(2)
        assertThat(parsedJson.stationTimesTable.rows[0].stationNm).isEqualTo("수서")
    }

    @ParameterizedTest
    @MethodSource("failCase")
    @DisplayName("역 시간표 API 실패 여부를 체크한다.")
    fun isFail(failResponse: String) {
        //given
        val response = objectMapper.readValue(failResponse, StationTimesDto.Response::class.java)

        //when
        val isFail = response.isFail()

        //then
        assertThat(isFail).isTrue()
    }

    companion object {
        @JvmStatic
        fun failCase(): Stream<String> = Stream.of(
            // list_total_count: 0
            """ 
            {
              "SearchSTNTimeTableByIDService": {
                "list_total_count": 0,
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
                  }
                ]
              }
            }
            """,
            // RESULT.CODE: INFO-001 성공 코드 아닐 시
            """
            {
              "SearchSTNTimeTableByIDService": {
                "list_total_count": 191,
                "RESULT": {
                  "CODE": "INFO-001",
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
                  }
                ]
              }
            }
            """,
            // row: 비어있을 시
            """
            {
              "SearchSTNTimeTableByIDService": {
                "list_total_count": 191,
                "RESULT": {
                  "CODE": "INFO-000",
                  "MESSAGE": "정상 처리되었습니다"
                },
                "row": []
              }
            }
            """
        )
    }
}