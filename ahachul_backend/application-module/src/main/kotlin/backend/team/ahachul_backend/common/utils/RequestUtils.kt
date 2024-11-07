package backend.team.ahachul_backend.common.utils

import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

class RequestUtils {

    companion object {
        fun setAttribute(key: String, value: Any) {
            RequestContextHolder.getRequestAttributes()
                    ?.setAttribute(key, value, RequestAttributes.SCOPE_REQUEST)
        }

        fun getAttribute(key: String): String? {
            return RequestContextHolder.getRequestAttributes()
                ?.getAttribute(key, RequestAttributes.SCOPE_REQUEST)
                ?.toString()
        }
    }
}