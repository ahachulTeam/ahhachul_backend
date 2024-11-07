package backend.team.ahachul_backend.common.config

import backend.team.ahachul_backend.common.interceptor.AuthenticationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig(
        private val authenticationInterceptor: AuthenticationInterceptor
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/**")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            // TODO 환경 분리
            .allowedOriginPatterns("https://app-dev.ahhachul.com", "https://dev.ahhachul.com", "http://localhost:3000", "http://localhost")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
