package backend.team.ahachul_backend.common.utils

import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

class RequestUtils {
    enum class Attribute (val key: String) {
        MEMBER_ID("memberId")
    }

    companion object {
        fun setAttribute(attribute: Attribute, value: Any) {
            RequestContextHolder.getRequestAttributes()
                    ?.setAttribute(attribute.key, value, RequestAttributes.SCOPE_REQUEST)

        }

        fun getAttribute(attribute: Attribute): String? {
            return RequestContextHolder.getRequestAttributes()
                ?.getAttribute(attribute.key, RequestAttributes.SCOPE_REQUEST)
                ?.toString()
        }
    }
}