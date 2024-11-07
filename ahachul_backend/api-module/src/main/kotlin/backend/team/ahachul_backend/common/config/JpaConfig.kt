package backend.team.ahachul_backend.common.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaConfig(
    @PersistenceContext
    val entityManager: EntityManager
) {

    companion object {
        private const val MEMBER_ID = "memberId"
        private const val UNAUTHORIZED_VALUE = "SYSTEM"
    }

    @Bean
    fun auditorProvider() = AuditorAware {
        val memberId: String = RequestUtils.getAttribute(MEMBER_ID) ?: UNAUTHORIZED_VALUE
        Optional.of(memberId)
    }

    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(entityManager)
}
