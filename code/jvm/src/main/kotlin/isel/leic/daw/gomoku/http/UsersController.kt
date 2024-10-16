package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.domain.AuthenticatedUser
import isel.leic.daw.gomoku.http.model.Problem
import isel.leic.daw.gomoku.http.model.UserCreateInputModel
import isel.leic.daw.gomoku.http.model.UserCreateTokenInputModel
import isel.leic.daw.gomoku.http.model.UserHomeOutputModel
import isel.leic.daw.gomoku.http.model.UserTokenCreateOutputModel
import isel.leic.daw.gomoku.services.TokenCreationError
import isel.leic.daw.gomoku.services.UserCreationError
import isel.leic.daw.gomoku.services.UserHomeOutputModelError
import isel.leic.daw.gomoku.services.UserHomeOutputModelResult
import isel.leic.daw.gomoku.services.UserService
import isel.leic.daw.gomoku.utils.Failure
import isel.leic.daw.gomoku.utils.Success
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UsersController(
    private val userService: UserService
) {

    @PostMapping(Uris.Users.CREATE)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.password)
        return when (res) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Users.byId(res.value).toASCIIString()
                ).build<Unit>()

            is Failure -> when (res.value) {
                UserCreationError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                UserCreationError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
            }
        }
    }

    @PostMapping(Uris.Users.TOKEN)
    fun token(
        @RequestBody input: UserCreateTokenInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val res = userService.createToken(input.username, input.password)
        return when (res) {
            is Success -> {
                response.addCookie(createCookie("t", res.value.tokenValue, true))
                response.addCookie(createCookie("gomoku", input.username, false))
                ResponseEntity.status(200)
                    .body(UserTokenCreateOutputModel(res.value.tokenValue))
            }

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid ->
                    Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        userService.revokeToken(user.token)
        return ResponseEntity.status(200).headers {
            it.add("Set-Cookie", "t=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Strict")
            it.add("Set-Cookie", "gomoku=; Max-Age=0; Path=/; Secure; SameSite=Strict")
        }.build<Unit>()
    }

    @GetMapping(Uris.Users.GET_BY_ID)
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        val res = userService.getUserById(id)
        return getResponseForUserIdRequest(res)
    }

    @GetMapping(Uris.Users.HOME)
    fun getUserHome(userAuthenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        logger.info("getUserHome: ${userAuthenticatedUser.user.username}")

        val res = userService.getUserHome(userAuthenticatedUser.user.id)
        return getResponseForUserIdRequest(res)
    }

    private fun createCookie(name: String, value: String?, httpOnly: Boolean): Cookie {
        logger.info("createCookie: $name, $value, $httpOnly")
        val cookieValue = value?.trim()
        val cookie = Cookie(name, cookieValue)
        cookie.path = "/"
        cookie.isHttpOnly = httpOnly
        cookie.secure = true
        cookie.setAttribute("SameSite", "Strict")
        cookie.maxAge = 3600

        return cookie
    }

    private fun getResponseForUserIdRequest(userHomeOutputModel: UserHomeOutputModelResult): ResponseEntity<*> {
        logger.info("getUserHome: $userHomeOutputModel")
        return when (userHomeOutputModel) {
            is Success -> ResponseEntity.status(200)
                .body(
                    UserHomeOutputModel(
                        userHomeOutputModel.value.id,
                        userHomeOutputModel.value.username,
                        userHomeOutputModel.value.rank,
                        userHomeOutputModel.value.gamesPlayed,
                        userHomeOutputModel.value.wins,
                        userHomeOutputModel.value.draws
                    )
                )

            is Failure -> when (userHomeOutputModel.value) {
                UserHomeOutputModelError.InvalidUser ->
                    Problem.response(400, Problem.userNotFound)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UsersController::class.java)
    }
}
