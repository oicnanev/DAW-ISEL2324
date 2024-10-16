package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.repository.jdbi.mapers.InstantMapper
import isel.leic.daw.gomoku.repository.jdbi.mapers.PasswordValidationInfoMapper
import isel.leic.daw.gomoku.repository.jdbi.mapers.TokenValidationInfoMapper
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(InstantMapper())

    return this
}
