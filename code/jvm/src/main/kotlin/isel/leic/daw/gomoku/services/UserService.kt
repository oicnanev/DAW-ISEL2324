package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.domain.UserDomain
import isel.leic.daw.gomoku.domain.entities.Token
import isel.leic.daw.gomoku.domain.entities.User
import isel.leic.daw.gomoku.http.model.UserHomeOutputModel
import isel.leic.daw.gomoku.repository.TransactionManager
import isel.leic.daw.gomoku.utils.Either
import isel.leic.daw.gomoku.utils.failure
import isel.leic.daw.gomoku.utils.success
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant
)

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
}
typealias UserCreationResult = Either<UserCreationError, Int>

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>

sealed class UserHomeOutputModelError {
    object InvalidUser : UserHomeOutputModelError()
}
typealias UserHomeOutputModelResult = Either<UserHomeOutputModelError, UserHomeOutputModel>

@Component
class UserService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UserDomain,
    private val clock: Clock
) {

    fun createUser(username: String, password: String): UserCreationResult {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserCreationError.InsecurePassword)
        }

        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)

        return transactionManager.run {
            val usersRepository = it.userRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                failure(UserCreationError.UserAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, passwordValidationInfo)
                success(id)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            failure(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.userRepository
            val user: User = usersRepository.getUserByUsername(username)
                ?: return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            if (!usersDomain.validatePassword(password, user.passwordValidation)) {
                return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val tokenValue = usersDomain.generateTokenValue()
            val now = clock.now()
            val newToken = Token(
                usersDomain.createTokenValidationInformation(tokenValue),
                user.id,
                createdAt = now,
                lastUsedAt = now
            )
            usersRepository.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
            Either.Right(
                TokenExternalInfo(
                    tokenValue,
                    usersDomain.getTokenExpiration(newToken)
                )
            )
        }
    }

    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.userRepository
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken = usersRepository.getTokenByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                usersRepository.updateTokenLastUsed(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }

    fun revokeToken(token: String): Boolean {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return transactionManager.run {
            it.userRepository.removeTokenByValidationInfo(tokenValidationInfo)
            true
        }
    }

    fun getUserHome(userId: Int): UserHomeOutputModelResult {
        return transactionManager.run {
            val usersRepository = it.userRepository
            val user = usersRepository.getUserById(userId)
            if (user == null) {
                failure(UserHomeOutputModelError.InvalidUser)
            } else {
                success(
                    UserHomeOutputModel(
                        id = user.id,
                        username = user.username,
                        rank = user.rank,
                        gamesPlayed = user.gamesPlayed,
                        wins = user.wins,
                        draws = user.draws
                    )
                )
            }
        }
    }

    fun getUserById(id: String): UserHomeOutputModelResult {
        return transactionManager.run {
            val usersRepository = it.userRepository
            val user = usersRepository.getUserById(id.toInt())
            if (user == null) {
                failure(UserHomeOutputModelError.InvalidUser)
            } else {
                success(
                    UserHomeOutputModel(
                        id = user.id,
                        username = user.username,
                        rank = user.rank,
                        gamesPlayed = user.gamesPlayed,
                        wins = user.wins,
                        draws = user.draws
                    )
                )
            }
        }
    }
}
