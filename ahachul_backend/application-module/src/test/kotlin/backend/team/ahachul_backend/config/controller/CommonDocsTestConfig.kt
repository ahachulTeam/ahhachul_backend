package backend.team.ahachul_backend.config.controller

import backend.team.ahachul_backend.common.interceptor.AuthenticationInterceptor
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
abstract class CommonDocsTestConfig {

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var authenticationInterceptor: AuthenticationInterceptor

    @BeforeEach
    fun setup() {
        BDDMockito.given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true)
    }

    protected fun commonResponseFields(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메시지")
        )
    }

    protected fun getFormatAttribute(format: String): Attributes.Attribute {
        return Attributes.Attribute("format", format)
    }

    protected fun getNewLine(): String {
        return " +\n"
    }

    protected fun getDocsRequest(): OperationRequestPreprocessor {
        return preprocessRequest(Preprocessors.prettyPrint())
    }

    protected fun getDocsResponse(): OperationResponsePreprocessor {
        return preprocessResponse(Preprocessors.prettyPrint())
    }

    protected fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}
