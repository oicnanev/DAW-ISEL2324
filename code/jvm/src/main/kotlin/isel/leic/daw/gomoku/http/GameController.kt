package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.domain.AuthenticatedUser
import isel.leic.daw.gomoku.http.model.InputCoordinates
import isel.leic.daw.gomoku.http.model.InputLobbyGame
import isel.leic.daw.gomoku.http.model.Problem
import isel.leic.daw.gomoku.http.model.WaitForPartner
import isel.leic.daw.gomoku.services.GameCreationError
import isel.leic.daw.gomoku.services.GameListingError
import isel.leic.daw.gomoku.services.GamePlayError
import isel.leic.daw.gomoku.services.GamePlayResult
import isel.leic.daw.gomoku.services.GameService
import isel.leic.daw.gomoku.services.LobbyError
import isel.leic.daw.gomoku.utils.Failure
import isel.leic.daw.gomoku.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(
    private val gameService: GameService
) {
    @GetMapping(Uris.Lobby.LOBBY)
    fun listLobbyGames(userAuthenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        // TODO: implement pagination
        return when (val res = gameService.listLobbyGames(userAuthenticatedUser)) {
            is Success ->
                ResponseEntity.status(200)
                    .header("Content-Type", "application/json")
                    .body(res.value)

            is Failure -> when (res.value) {
                GameListingError.InvalidToken ->
                    Problem.response(400, Problem.invalidToken)
            }
        }
    }

    @PostMapping(Uris.Lobby.LOBBY)
    fun addGameToLobby(
        userAuthenticatedUser: AuthenticatedUser,
        @RequestBody lobbyGame: InputLobbyGame
    ): ResponseEntity<*> {
        return when (
            val res =
                gameService.addGameToLobby(
                    userAuthenticatedUser,
                    lobbyGame.variant,
                    lobbyGame.opening,
                    lobbyGame.boardSize
                )
        ) {
            is Success ->
                ResponseEntity.status(201)
                    .header("Location", Uris.Lobby.byId(res.value).toString()).build<Unit>()

            is Failure -> when (res.value) {
                LobbyError.GameAlreadyEnqueued ->
                    Problem.response(400, Problem.gameAlreadyInLobby)

                LobbyError.InvalidVariant ->
                    Problem.response(406, Problem.invalidVariant)

                LobbyError.InvalidOpening ->
                    Problem.response(406, Problem.invalidOpening)

                LobbyError.InvalidBoardSize ->
                    Problem.response(406, Problem.invalidBoardSize)

                else -> Problem.response(400, Problem.invalidRequestContent)
            }
        }
    }

    @DeleteMapping(Uris.Lobby.LOBBYID)
    fun deleteGameFromLobby(
        userAuthenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameService.giveUpGameFromLobby(id, userAuthenticatedUser)) {
            is Success ->
                ResponseEntity.status(200)
                    .body("Lobby game " + res.value + " deleted")

            is Failure -> when (res.value) {
                LobbyError.InvalidGame -> Problem.response(400, Problem.gameAlreadyInLobby)
                LobbyError.InvalidRequestContent -> Problem.response(400, Problem.gameAlreadyInLobby)
                LobbyError.InvalidUser -> Problem.response(400, Problem.userAlreadyHasGameInLobby)
                LobbyError.GameAlreadyEnqueued -> Problem.response(400, Problem.gameAlreadyInLobby)
                LobbyError.InvalidBoardSize -> Problem.response(400, Problem.invalidBoardSize)
                LobbyError.InvalidOpening -> Problem.response(400, Problem.invalidOpening)
                LobbyError.InvalidVariant -> Problem.response(400, Problem.invalidVariant)
                LobbyError.WaitForPartner -> Problem.response(400, Problem.waitForPartner)
            }
        }
    }

    @PostMapping(Uris.Lobby.JOINGAMEID)
    fun joinGame(
        userAuthenticatedUser: AuthenticatedUser,
        @PathVariable lobbyGameId: Int
    ): ResponseEntity<*> {
        return when (val res = gameService.joinGame(userAuthenticatedUser, lobbyGameId)) {
            is Success ->
                ResponseEntity.status(201)
                    .header(
                        "Location",
                        Uris.Game.byId(res.value).toASCIIString()
                    ).body(res)

            is Failure -> when (res.value) {
                GameCreationError.InvalidGame -> Problem.response(400, Problem.gameAlreadyInLobby)
                GameCreationError.InvalidRequestContent -> Problem.response(400, Problem.gameAlreadyInLobby)
                GameCreationError.InvalidUser -> Problem.response(400, Problem.userAlreadyHasGameInLobby)
            }
        }
    }

    @GetMapping(Uris.Lobby.CHECKMATCH)
    fun checkGamePartner(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.checkGameAlreadyHaveAPartner(authenticatedUser)) {
            is Success ->
                ResponseEntity.status(301) // not sure if this is the correct status code, redirect
                    .header("Location", Uris.Game.byId(res.value).toString())
                    .body(res)

            is Failure -> when (res.value) {
                LobbyError.WaitForPartner ->
                    ResponseEntity.status(200)
                        .header("Retry-After", "5")
                        .header("Content-Type", "application/json")
                        .body(WaitForPartner())

                LobbyError.InvalidUser ->
                    Problem.response(400, Problem.invalidPlayer)

                else -> Problem.response(400, Problem.invalidRequestContent)
            }
        }
    }

    @GetMapping(Uris.Game.GAMEID)
    fun getGameById(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return gameResponse(gameService.getGameById(authenticatedUser, id))
    }

    @PostMapping(Uris.Game.GAMEID)
    fun playMove(
        @PathVariable id: Int,
        @RequestBody inputCoordinates: InputCoordinates,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        return when (
            val res = gameService.playMove(
                id,
                authenticatedUser,
                inputCoordinates.x,
                inputCoordinates.y
            )
        ) {
            is Success -> {
                ResponseEntity.status(201)
                    .header("Location", Uris.Users.byId(res.value.id).toASCIIString())
                    //build<Unit>()
                    .body(res.value)
            }

            is Failure -> gameResponseError(res.value)
        }
    }

    @PostMapping(Uris.Game.GIVEUP)
    fun giveUpGame(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        return gameResponse(gameService.giveUpGame(authenticatedUser))
    }

    private fun gameResponse(gamePlayResult: GamePlayResult): ResponseEntity<*> {
        return when (gamePlayResult) {
            is Success ->
                ResponseEntity.status(200)
                    .header("Content-Type", "application/json")
                    .body(gamePlayResult.value)

            is Failure ->
                gameResponseError(gamePlayResult.value)
        }
    }

    private fun gameResponseError(failure: GamePlayError): ResponseEntity<*> {
        return when (failure) {
            GamePlayError.InvalidGame ->
                Problem.response(400, Problem.invalidGame)

            GamePlayError.InvalidRequestContent ->
                Problem.response(400, Problem.invalidRequestContent)

            GamePlayError.InvalidMove ->
                Problem.response(400, Problem.invalidMove)

            GamePlayError.InvalidPlayer ->
                Problem.response(400, Problem.invalidPlayer)

            GamePlayError.InvalidPosition ->
                Problem.response(400, Problem.invalidPosition)

            GamePlayError.InvalidToken ->
                Problem.response(400, Problem.invalidToken)
        }
    }
}
