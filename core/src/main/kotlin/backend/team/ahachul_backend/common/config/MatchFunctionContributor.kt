package backend.team.ahachul_backend.common.config

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.type.BasicTypeReference
import org.hibernate.type.SqlTypes
import org.hibernate.type.StandardBasicTypes
import org.springframework.stereotype.Component

class MatchFunctionContributor : FunctionContributor {

    override fun contributeFunctions(functionContributions: FunctionContributions) {
        val functionRegistry = functionContributions.functionRegistry

        val doubleType = functionContributions.typeConfiguration
            .basicTypeRegistry
            .resolve(BasicTypeReference(
                "double",
                Double::class.java,
                SqlTypes.DOUBLE
           ))

        functionRegistry.registerPattern(
            "match_natural",
            "MATCH(?1, ?2) AGAINST (?3 IN NATURAL LANGUAGE MODE)",
            doubleType
        )
    }
}
