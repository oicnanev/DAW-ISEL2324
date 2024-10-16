package isel.leic.daw.gomoku.http.pipeline

import isel.leic.daw.gomoku.domain.AuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: RequestTokenProcessor
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any {
            it.parameterType == AuthenticatedUser::class.java
        }
        ) {
            var user: AuthenticatedUser? = null

            if (request.getHeader(NAME_AUTHORIZATION_HEADER) != null) {
                logger.info("Authorization Interceptor - header: {}", request.getHeader(NAME_AUTHORIZATION_HEADER))
                user = authorizationHeaderProcessor
                    .processAuthorizationHeaderValue(request.getHeader(NAME_AUTHORIZATION_HEADER))
            }

            if (request.cookies != null) {
                for (cookie in request.cookies) {
                    logger.info("Authorization Interceptor - cookie: {}", cookie)
                    logger.info("Authorization Interceptor - cookie name: {}", cookie.name)
                }
                logger.info("Authorization Interceptor - cookies: {}", request.cookies)
                user = request.cookies
                    .find { it.name == "t" }
                    ?.let { authorizationHeaderProcessor.processAuthorizationHeaderValue("bearer " + it.value) }
            }
            // enforce authentication

            return if (user == null) {
                logger.info("Authorization Interceptor - no user")
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                false
            } else {
                logger.info("Authorization Interceptor- pass user to AuthenticateUserArgumentResolver - user: {}", user)
                AuthenticatedUserArgumentResolver.addUserTo(user, request)
                true
            }
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}
