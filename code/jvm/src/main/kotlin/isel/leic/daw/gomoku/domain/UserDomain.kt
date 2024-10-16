package isel.leic.daw.gomoku.domain

import isel.leic.daw.gomoku.domain.entities.Token
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class UserDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UserDomainConfig
) {
    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == config.tokenSizeInBytes
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun validatePassword(password: String, validationInfo: PasswordValidationInfo) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo
    )

    fun createPasswordValidationInformation(password: String) = PasswordValidationInfo(
        validationInfo = passwordEncoder.encode(password)
    )

    fun isTokenTimeValid(
        clock: Clock,
        token: Token
    ): Boolean {
        val now = clock.now()
        return token.createdAt <= now &&
            (now - token.createdAt) <= config.tokenTtl &&
            (now - token.lastUsedAt) <= config.tokenRollingTtl
    }

    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }

    fun createTokenValidationInformation(token: String): TokenValidationInfo =
        tokenEncoder.createValidationInformation(token)

    fun isSafePassword(password: String): Boolean {
        /* For safety reasons, the password must have at least 8 characters,
        1 digit, 1 uppercase letter, 1 lowercase letter and 1 special character. */
        // https://stackoverflow.com/questions/69928312/password-validation-with-kotlin
        if (password.length < 8) return false // at least 8 characters
        if (password.firstOrNull { it.isDigit() } == null) return false // at least 1 digit
        if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false // at least 1 uppercase letter
        if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false // at least 1 lowercase letter
        if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false // at least 1 special character

        return true
    }

    val maxNumberOfTokensPerUser = config.maxTokensPerUser
}
