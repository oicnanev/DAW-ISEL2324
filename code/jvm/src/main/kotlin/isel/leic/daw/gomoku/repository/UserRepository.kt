package isel.leic.daw.gomoku.repository

import isel.leic.daw.gomoku.domain.PasswordValidationInfo
import isel.leic.daw.gomoku.domain.TokenValidationInfo
import isel.leic.daw.gomoku.domain.entities.Token
import isel.leic.daw.gomoku.domain.entities.User
import kotlinx.datetime.Instant

interface UserRepository {
    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
        rank: Int = 0,
        gamesPlayed: Int = 0,
        wins: Int = 0,
        draws: Int = 0
    ): Int

    fun getUserById(id: Int): User?

    fun getUserUsernameById(id: Int): String?

    fun getUserByUsername(username: String): User?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(token: Token, maxTokens: Int)

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int

    fun getUserIDByUsername(username1: String): Int

    fun updateUserStatistics(userId: Int, rank: Int, gamesPlayed: Int, wins: Int, draws: Int)
}
