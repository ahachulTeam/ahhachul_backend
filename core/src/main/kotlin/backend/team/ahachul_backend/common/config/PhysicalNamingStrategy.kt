package backend.team.ahachul_backend.common.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

class PhysicalNamingStrategy: PhysicalNamingStrategyStandardImpl() {

    companion object {
        const val PREFIX: String = "tb_"
        const val ENTITY: String = "_entity"
    }

    override fun toPhysicalTableName(logicalName: Identifier?, context: JdbcEnvironment?): Identifier {
        return convertTableName(logicalName)
    }

    override fun toPhysicalColumnName(logicalName: Identifier?, context: JdbcEnvironment?): Identifier {
        return convertName(logicalName)
    }

    private fun convertName(identifier: Identifier?): Identifier {
        val regex = "([a-z])([A-Z])"
        val replacement = "$1_$2"
        val newName = identifier!!.text.replace(regex.toRegex(), replacement).lowercase()
        return Identifier.toIdentifier(newName)
    }

    private fun convertTableName(identifier: Identifier?): Identifier {
        val regex = "([a-z])([A-Z])"
        val replacement = "$1_$2"
        val newName = identifier!!.text.replace(regex.toRegex(), replacement).lowercase()
        return Identifier.toIdentifier(PREFIX + newName.replace(ENTITY, ""))
    }
}