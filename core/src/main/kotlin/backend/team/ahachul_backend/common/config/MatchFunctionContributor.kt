package backend.team.ahachul_backend.common.config

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.type.StandardBasicTypes
import org.springframework.stereotype.Component

@Component
class MatchFunctionContributor : FunctionContributor {

    override fun contributeFunctions(functionContributions: FunctionContributions) {

        val functionRegistry = functionContributions.functionRegistry
        val typeRegistry = functionContributions.typeConfiguration.basicTypeRegistry
        val doubleType = typeRegistry.resolve(StandardBasicTypes.DOUBLE)

        functionRegistry.registerPattern(
            "match_boolean",
            "MATCH(?1, ?2) AGAINST (?3 IN BOOLEAN MODE)",
            doubleType
        )
    }
}
