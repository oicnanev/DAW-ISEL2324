package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.TestClock
import isel.leic.daw.gomoku.domain.AuthenticatedUser
import isel.leic.daw.gomoku.domain.GameDomain
import isel.leic.daw.gomoku.domain.Sha256TokenEncoder
import isel.leic.daw.gomoku.domain.UserDomain
import isel.leic.daw.gomoku.domain.UserDomainConfig
import isel.leic.daw.gomoku.repository.jdbi.JdbiTransactionManager
import isel.leic.daw.gomoku.repository.jdbi.configureWithAppRequirements
import isel.leic.daw.gomoku.utils.Either
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.random.Random
import kotlin.test.DefaultAsserter
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class GameServiceTests {
    @Test
    fun `can add game to lobby and retrieve partner status`() {
        // given: a GameService, a user and a game type
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username, password)
        val token = when (
            val createTokenResult = userService.createToken(username, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user = userService.getUserByToken(token.tokenValue)
        val authenticatedUser = AuthenticatedUser(user!!, token.tokenValue)

        // when: adding a game to lobby
        val lobbyGameId = gameService.addGameToLobby(
            authenticatedUser,
            "freestyle simple",
            "open",
            15
        )

        // then: the game is added to lobby
        assertNotNull(lobbyGameId)

        // when: checking if the user has an active game in lobby
        when (
            val hasUserAnActiveGameInLobby = gameService.checkGameAlreadyHaveAPartner(authenticatedUser)
        ) {
            is Either.Left -> hasUserAnActiveGameInLobby.value

            is Either.Right -> DefaultAsserter.fail(
                "Should fail because it has no partner yet: '${hasUserAnActiveGameInLobby.value}'"
            )
        }
    }

    @Test
    fun `can join game in lobby`() {
        // given a game and user services, 2 users and a game in lobby
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        // when: user2 joins the game
        when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")

            // then: the game is joined
            is Either.Right -> joinGameResult.value
        }
    }

    @Test
    fun `can give up game in lobby`() {
        // given a game and user services, a user and a game in lobby
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username, password)
        val token = when (
            val createTokenResult = userService.createToken(username, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user = userService.getUserByToken(token.tokenValue)
        val authenticatedUser = AuthenticatedUser(user!!, token.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }

        // when: user gives up the game
        when (
            val giveUpGameResult = gameService.giveUpGameFromLobby(lobbyGameId, authenticatedUser)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")

            // then: the game is given up
            is Either.Right -> giveUpGameResult.value
        }
    }

    @Test
    fun `can get game`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }

        // when user gets the game
        when (
            val getGameResult = gameService.getGameById(
                authenticatedUser2,
                gameId
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")

            // Then the game is retrieved
            is Either.Right -> getGameResult.value
        }
    }

    @Test
    fun `can play a move`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        val game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val player = if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2

        // when user plays a move
        when (
            val playMoveResult =
                gameService.playMove(gameId, player, 1, 1)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")

            // Then the move is played
            is Either.Right -> playMoveResult.value
        }
    }

    @Test
    fun `can give up game`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        when (
            val joinGameResult = gameService.joinGame(
                authenticatedUser2,
                lobbyGameId
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }

        // when user gives up the game
        when (
            val giveUpGameResult = gameService.giveUpGame(authenticatedUser2)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")

            // Then the game is given up
            is Either.Right -> giveUpGameResult.value
        }
    }

    @Test
    fun `can win horizontally`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        var game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val firstPlayer = if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2
        val secondPlayer = if (currentPlayer == 1) authenticatedUser2 else authenticatedUser1

        // when user plays a move
        for (i in 0..3) {
            gameService.playMove(gameId, firstPlayer, i, 0)
            gameService.playMove(gameId, secondPlayer, i, 5)
        }
        gameService.playMove(gameId, firstPlayer, 4, 0)

        game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }

        // then the game is won
        if (firstPlayer == authenticatedUser1) {
            assertTrue { game.gameState == "player1_won" }
        } else {
            assertTrue { game.gameState == "player2_won" }
        }
    }

    @Test
    fun `can win vertically`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        var game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val firstPlayer =
            if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2
        val secondPlayer =
            if (currentPlayer == 1) authenticatedUser2 else authenticatedUser1

        // when user plays a move
        for (i in 0..3) {
            gameService.playMove(gameId, firstPlayer, 0, i)
            gameService.playMove(gameId, secondPlayer, 5, i)
        }
        gameService.playMove(gameId, firstPlayer, 0, 4)

        game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }

        // then the game is won
        if (firstPlayer == authenticatedUser1) {
            assertTrue { game.gameState == "player1_won" }
        } else {
            assertTrue { game.gameState == "player2_won" }
        }
    }

    @Test
    fun `can win diagonally (top left to bottom right)`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        var game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val firstPlayer =
            if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2
        val secondPlayer =
            if (currentPlayer == 1) authenticatedUser2 else authenticatedUser1

        // when user plays a move
        for (i in 0..3) {
            gameService.playMove(gameId, firstPlayer, i, i)
            gameService.playMove(gameId, secondPlayer, 9, i)
        }
        gameService.playMove(gameId, firstPlayer, 4, 4)

        game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }

        // then the game is won
        if (firstPlayer == authenticatedUser1) {
            assertTrue { game.gameState == "player1_won" }
        } else {
            assertTrue { game.gameState == "player2_won" }
        }
    }

    @Test
    fun `can win diagonally (top right to bottom left)`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        var game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val firstPlayer = if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2
        val secondPlayer = if (currentPlayer == 1) authenticatedUser2 else authenticatedUser1

        // when user plays a move
        gameService.playMove(gameId, firstPlayer, 14, 0)
        gameService.playMove(gameId, secondPlayer, 9, 0)
        gameService.playMove(gameId, firstPlayer, 13, 1)
        gameService.playMove(gameId, secondPlayer, 9, 1)
        gameService.playMove(gameId, firstPlayer, 12, 2)
        gameService.playMove(gameId, secondPlayer, 9, 2)
        gameService.playMove(gameId, firstPlayer, 11, 3)
        gameService.playMove(gameId, secondPlayer, 9, 3)
        gameService.playMove(gameId, firstPlayer, 10, 4)

        game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }

        // then the game is won
        if (firstPlayer == authenticatedUser1) {
            assertTrue { game.gameState == "player1_won" }
        } else {
            assertTrue { game.gameState == "player2_won" }
        }
    }

    @Test
    fun `can draw`() {
        // given a game and user services, 2 users and a game
        val gameService = createGameService()
        val userService = createUsersService(TestClock())
        val username1 = newTestUserName()
        val username2 = newTestUserName()
        val password = "P455w0d?!"
        userService.createUser(username1, password)
        userService.createUser(username2, password)
        val token1 = when (
            val createTokenResult = userService.createToken(username1, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val token2 = when (
            val createTokenResult = userService.createToken(username2, password)
        ) {
            is Either.Left -> DefaultAsserter.fail("Token creation should be successful: '${createTokenResult.value}'")
            is Either.Right -> createTokenResult.value
        }
        val user1 = userService.getUserByToken(token1.tokenValue)
        val user2 = userService.getUserByToken(token2.tokenValue)
        val authenticatedUser1 = AuthenticatedUser(user1!!, token1.tokenValue)
        val authenticatedUser2 = AuthenticatedUser(user2!!, token2.tokenValue)
        val lobbyGameId = when (
            val addGameToLobbyResult = gameService.addGameToLobby(
                authenticatedUser1,
                "freestyle simple",
                "open",
                15
            )
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> addGameToLobbyResult.value
        }
        val gameId = when (
            val joinGameResult =
                gameService.joinGame(authenticatedUser2, lobbyGameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game creation should be successful")
            is Either.Right -> joinGameResult.value
        }
        var game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }
        val currentPlayer = game.currentPlayer
        val firstPlayer = if (currentPlayer == 1) authenticatedUser1 else authenticatedUser2
        val secondPlayer = if (currentPlayer == 1) authenticatedUser2 else authenticatedUser1

        // when user plays a move
        for (i in 0..3) {
            gameService.playMove(gameId, firstPlayer, i, 0)
            gameService.playMove(gameId, secondPlayer, i, 1)
            gameService.playMove(gameId, firstPlayer, i, 2)
            gameService.playMove(gameId, secondPlayer, i, 3)
            gameService.playMove(gameId, firstPlayer, i, 4)
            gameService.playMove(gameId, secondPlayer, i, 5)
            gameService.playMove(gameId, firstPlayer, i, 6)
            gameService.playMove(gameId, secondPlayer, i, 7)
            gameService.playMove(gameId, firstPlayer, i, 8)
            gameService.playMove(gameId, secondPlayer, i, 9)
            gameService.playMove(gameId, firstPlayer, i, 10)
            gameService.playMove(gameId, secondPlayer, i, 11)
            gameService.playMove(gameId, firstPlayer, i, 12)
            gameService.playMove(gameId, secondPlayer, i, 13)
        }
        for (i in 4..7) {
            gameService.playMove(gameId, firstPlayer, i, 1)
            gameService.playMove(gameId, secondPlayer, i, 2)
            gameService.playMove(gameId, firstPlayer, i, 3)
            gameService.playMove(gameId, secondPlayer, i, 4)
            gameService.playMove(gameId, firstPlayer, i, 5)
            gameService.playMove(gameId, secondPlayer, i, 6)
            gameService.playMove(gameId, firstPlayer, i, 7)
            gameService.playMove(gameId, secondPlayer, i, 8)
            gameService.playMove(gameId, firstPlayer, i, 9)
            gameService.playMove(gameId, secondPlayer, i, 10)
            gameService.playMove(gameId, firstPlayer, i, 11)
            gameService.playMove(gameId, secondPlayer, i, 12)
            gameService.playMove(gameId, firstPlayer, i, 13)
            gameService.playMove(gameId, secondPlayer, i, 14)
        }
        for (i in 8..11) {
            gameService.playMove(gameId, firstPlayer, i, 0)
            gameService.playMove(gameId, secondPlayer, i, 1)
            gameService.playMove(gameId, firstPlayer, i, 2)
            gameService.playMove(gameId, secondPlayer, i, 3)
            gameService.playMove(gameId, firstPlayer, i, 4)
            gameService.playMove(gameId, secondPlayer, i, 5)
            gameService.playMove(gameId, firstPlayer, i, 6)
            gameService.playMove(gameId, secondPlayer, i, 7)
            gameService.playMove(gameId, firstPlayer, i, 8)
            gameService.playMove(gameId, secondPlayer, i, 9)
            gameService.playMove(gameId, firstPlayer, i, 10)
            gameService.playMove(gameId, secondPlayer, i, 11)
            gameService.playMove(gameId, firstPlayer, i, 12)
            gameService.playMove(gameId, secondPlayer, i, 13)
        }
        for (i in 12..14) {
            gameService.playMove(gameId, firstPlayer, i, 1)
            gameService.playMove(gameId, secondPlayer, i, 2)
            gameService.playMove(gameId, firstPlayer, i, 3)
            gameService.playMove(gameId, secondPlayer, i, 4)
            gameService.playMove(gameId, firstPlayer, i, 5)
            gameService.playMove(gameId, secondPlayer, i, 6)
            gameService.playMove(gameId, firstPlayer, i, 7)
            gameService.playMove(gameId, secondPlayer, i, 8)
            gameService.playMove(gameId, firstPlayer, i, 9)
            gameService.playMove(gameId, secondPlayer, i, 10)
            gameService.playMove(gameId, firstPlayer, i, 11)
            gameService.playMove(gameId, secondPlayer, i, 12)
            gameService.playMove(gameId, firstPlayer, i, 13)
            gameService.playMove(gameId, secondPlayer, i, 14)
        }
        gameService.playMove(gameId, firstPlayer, 0, 14)
        gameService.playMove(gameId, secondPlayer, 4, 0)
        gameService.playMove(gameId, firstPlayer, 1, 14)
        gameService.playMove(gameId, secondPlayer, 5, 0)
        gameService.playMove(gameId, firstPlayer, 2, 14)
        gameService.playMove(gameId, secondPlayer, 6, 0)
        gameService.playMove(gameId, firstPlayer, 3, 14)
        gameService.playMove(gameId, secondPlayer, 7, 0)

        gameService.playMove(gameId, firstPlayer, 8, 14)
        gameService.playMove(gameId, secondPlayer, 12, 0)
        gameService.playMove(gameId, firstPlayer, 9, 14)
        gameService.playMove(gameId, secondPlayer, 12, 0)
        gameService.playMove(gameId, firstPlayer, 10, 14)
        gameService.playMove(gameId, secondPlayer, 13, 0)
        gameService.playMove(gameId, firstPlayer, 11, 14)

        game = when (
            val getGameResult = gameService.getGameById(authenticatedUser2, gameId)
        ) {
            is Either.Left -> DefaultAsserter.fail("Game retrieve should be successful")
            is Either.Right -> getGameResult.value
        }

        // then the game is won
        assertTrue { game.gameState == "draw" }
    }

    companion object {
        private fun createGameService(): GameService {
            return GameService(
                JdbiTransactionManager(jdbi),
                GameDomain(),
                TestClock()
            )
        }

        private fun createUsersService(
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3
        ) = UserService(
            JdbiTransactionManager(jdbi),
            UserDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UserDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser
                )
            ),
            testClock
        )

        private fun newTestUserName() = "user-${Math.abs(Random.nextLong())}"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
            }
        ).configureWithAppRequirements()

        // If used without the docker-compose.yml file, the following line should be uncommented:
        /* private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://minedust.ddns.net:5432/gomokunew?user=postgres&password=4w5_Yd4xee35$")
            }
        ).configureWithAppRequirements()
         */
    }
}
