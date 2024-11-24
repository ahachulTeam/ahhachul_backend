package backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.comment.domain.model.CommentVisibility
import backend.team.ahachul_backend.api.comment.domain.model.PostType
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CreateCommentDtoTest {

    @ParameterizedTest
    @CsvSource("1, 코멘트 내용, true", "2, 코멘트 내용내용, false")
    fun 자식_코멘트_생성_요청_시_비공개_여부_요청_값이_들어오면_예외처리(upperCommentId: Long, content: String, isPrivate: Boolean) {
        //when, then
        assertThatThrownBy { CreateCommentDto.Request(upperCommentId, content, isPrivate) }
            .isInstanceOf(CommonException::class.java)
            .hasMessage(ResponseCode.BAD_REQUEST.message)
    }

    @ParameterizedTest
    @CsvSource("1, 코멘트 내용", "2, 코멘트 내용내용")
    fun 자식_코멘트_생성_요청_시_비공개_여부_없을때_정상(upperCommentId: Long, content: String) {
        //given
        val request = CreateCommentDto.Request(upperCommentId, content, null)

        //when
        val command = request.toCommand(1L, PostType.LOST)

        //then
        assertThat(command.upperCommentId).isEqualTo(upperCommentId)
        assertThat(command.content).isEqualTo(content)
        assertThat(command.visibility).isEqualTo(CommentVisibility.PUBLIC)
    }
}