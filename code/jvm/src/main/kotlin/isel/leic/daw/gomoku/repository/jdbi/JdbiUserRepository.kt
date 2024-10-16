package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.domain.PasswordValidationInfo
import isel.leic.daw.gomoku.domain.TokenValidationInfo
import isel.leic.daw.gomoku.domain.entities.Token
import isel.leic.daw.gomoku.domain.entities.User
import isel.leic.daw.gomoku.repository.UserRepository
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

class JdbiUserRepository(
    private val handle: Handle
) : UserRepository {
    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from dbo.users where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
        rank: Int,
        gamesPlayed: Int,
        wins: Int,
        draws: Int
    ): Int {
        return handle.createUpdate(
            """
            insert into dbo.users (username, password_validation) values (:username, :password_validation)
            """
        )
            .bind("username", username)
            .bind("password_validation", passwordValidation.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from dbo.users where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun createToken(token: Token, maxTokens: Int) {
        val deletions = handle.createUpdate(
            """
            delete from dbo.token 
            where user_id = :user_id 
                and token_validation in (
                    select token_validation from dbo.token where user_id = :user_id 
                        order by last_used_at desc offset :offset
                )
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("offset", maxTokens - 1)
            .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle.createUpdate(
            """
                insert into dbo.token(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate(
            """
                update dbo.token
                set last_used_at = :last_used_at
                where token_validation = :validation_information
            """.trimIndent()
        )
            .bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
            select id, username, password_validation, token_validation, created_at, last_used_at
            from dbo.users as u
            inner join dbo.token as t
            on u.id = t.user_id
            where token_validation = :validation_information
            """.trimIndent()
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        return handle.createUpdate(
            """
                delete from dbo.token
                where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getUserIDByUsername(username1: String): Int {
        return handle.createQuery("select * from dbo.users where username = :username1")
            .bind("username1", username1)
            .mapTo<Int>()
            .single()
    }

    override fun getUserById(id: Int): User? =
        handle.createQuery("select * from dbo.users where id = :id")
            .bind("id", id)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserUsernameById(id: Int): String? =
        handle.createQuery("select username from dbo.users where id = :id")
            .bind("id", id)
            .mapTo<String>()
            .singleOrNull()

    override fun updateUserStatistics(userId: Int, rank: Int, gamesPlayed: Int, wins: Int, draws: Int) {
        handle.createUpdate(
            """
                update dbo.users
                set rank = :rank, games_played = :games_played, wins = :wins, draws = :draws
                where id = :user_id
            """.trimIndent()
        )
            .bind("user_id", userId)
            .bind("rank", rank)
            .bind("games_played", gamesPlayed)
            .bind("wins", wins)
            .bind("draws", draws)
            .execute()
    }

    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val passwordValidation: PasswordValidationInfo,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(id, username, passwordValidation),
                Token(
                    tokenValidation,
                    id,
                    Instant.fromEpochSeconds(createdAt),
                    Instant.fromEpochSeconds(lastUsedAt)
                )
            )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JdbiUserRepository::class.java)
    }
}
