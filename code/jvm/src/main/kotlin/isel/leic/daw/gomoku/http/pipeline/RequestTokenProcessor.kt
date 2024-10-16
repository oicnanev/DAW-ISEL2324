package isel.leic.daw.gomoku.http.pipeline

import isel.leic.daw.gomoku.domain.AuthenticatedUser
import isel.leic.daw.gomoku.services.UserService
import org.springframework.stereotype.Component

@Component
class RequestTokenProcessor(
    val userService: UserService
) {
    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        logger.info("Authorization Interceptor - header: {}", authorizationValue)
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")

        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }

        return userService.getUserByToken(parts[1])?.let {
            AuthenticatedUser(it, parts[1])
        }
    }

    companion object {
        const val SCHEME = "bearer"
        private val logger = org.slf4j.LoggerFactory.getLogger(RequestTokenProcessor::class.java)
    }
}
