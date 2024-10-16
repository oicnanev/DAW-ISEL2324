package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.domain.PasswordValidationInfo
import isel.leic.daw.gomoku.domain.entities.Board
import isel.leic.daw.gomoku.domain.entities.BoardSize
import isel.leic.daw.gomoku.domain.entities.BoardUserData
import isel.leic.daw.gomoku.domain.entities.Game
import isel.leic.daw.gomoku.domain.entities.GameOpening
import isel.leic.daw.gomoku.domain.entities.GameVariant
import isel.leic.daw.gomoku.domain.entities.User
import kotlinx.datetime.Clock
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JdbiGameRepositoryTests {
    @Test
    fun `can add game to lobby and retrieve partner status`() = runWithHandle { handle ->
        // given: a GameRepository, a user and a game type
        val gameRepo = JdbiGameRepository(handle)
        val user = createATestUser(JdbiUserRepository(handle))
        val gameTypeId = gameRepo.getGameTypeIdByVariantOpeningAndBoardSize(
            GameVariant("freestyle simple"),
            GameOpening("open"),
            BoardSize(15)
        )

        // when: adding a game to lobby
        val lobbyGameId = gameRepo.addGameToLobby(user.id, gameTypeId, Clock.System.now())

        // then: the game is added to lobby
        assertNotNull(lobbyGameId)

        // when: checking if the user has an active game in lobby
        val hasUserAnActiveGameInLobby = gameRepo.hasUserAnActiveGameInLobby(user.id)
        assertTrue { hasUserAnActiveGameInLobby }
    }

    @Test
    fun `can give up game in lobby`() = runWithHandle { handle ->
        // given: a GameRepository, a user and a game type
        val gameRepo = JdbiGameRepository(handle)
        val user = createATestUser(JdbiUserRepository(handle))
        val gameTypeId = gameRepo.getGameTypeIdByVariantOpeningAndBoardSize(
            GameVariant("freestyle simple"),
            GameOpening("open"),
            BoardSize(15)
        )
        val lobbyGameId = gameRepo.addGameToLobby(user.id, gameTypeId, Clock.System.now())

        // when: giving up a game in lobby
        gameRepo.deactivateLobbyGame(lobbyGameId)

        // then: the game is removed from lobby
        val hasUserAnActiveGameInLobby = gameRepo.hasUserAnActiveGameInLobby(user.id)
        assertTrue { !hasUserAnActiveGameInLobby }
    }

    @Test
    fun `can list lobby games`() = runWithHandle { handle ->
        // given: a GameRepository
        val repo = JdbiGameRepository(handle)

        // when: listing lobby games
        val lobbyGames = repo.listLobbyGames()

        // then: there are lobby games
        assertNotNull(lobbyGames)
        assert(lobbyGames.isNotEmpty())
    }

    @Test
    fun `can join existing game in lobby`() = runWithHandle { handle ->
        // given: a GameRepository, 2 users and a game type
        val gameRepo = JdbiGameRepository(handle)
        val user = createATestUser(JdbiUserRepository(handle))
        val gameTypeId = gameRepo.getGameTypeIdByVariantOpeningAndBoardSize(
            GameVariant("freestyle simple"),
            GameOpening("open"),
            BoardSize(15)
        )
        gameRepo.addGameToLobby(user.id, gameTypeId, Clock.System.now())
        val partner = createATestUser(JdbiUserRepository(handle))
        val boardUserData1 = BoardUserData(user.id, "b", null)
        val boardUserData2 = BoardUserData(partner.id, "w", null)
        val board = Board(15, boardUserData1, boardUserData2)
        val now = Clock.System.now()
        val game = Game(null, gameTypeId, board, user.id, partner.id, 1, "in-progress", now, now)

        // when: joining an existing game in lobby
        val joinedGameId = gameRepo.createGame(game)

        // then: the game is joined
        assertNotNull(joinedGameId)
    }

    @Test
    fun `can retrieve active game and give up`() = runWithHandle { handle ->
        // given: a GameRepository, 2 users and a game type
        val gameRepo = JdbiGameRepository(handle)
        val user = createATestUser(JdbiUserRepository(handle))
        val gameTypeId = gameRepo.getGameTypeIdByVariantOpeningAndBoardSize(
            GameVariant("freestyle simple"),
            GameOpening("open"),
            BoardSize(15)
        )
        gameRepo.addGameToLobby(user.id, gameTypeId, Clock.System.now())
        val partner = createATestUser(JdbiUserRepository(handle))
        val boardUserData1 = BoardUserData(user.id, "b", null)
        val boardUserData2 = BoardUserData(partner.id, "w", null)
        val board = Board(15, boardUserData1, boardUserData2)
        val now = Clock.System.now()
        val gameRepresentation = Game(null, gameTypeId, board, user.id, partner.id, 1, "in-progress", now, now)
        val joinedGameId = gameRepo.createGame(gameRepresentation)

        // when: retrieving an active game
        val game = gameRepo.getGameById(joinedGameId)

        // then: the game is retrieved
        assertNotNull(game)

        // when: giving up an active game
        gameRepo.updateGameState(game.copy(state = "player1_won"), game.id!!, now.epochSeconds)

        // then: the game is given up
        val updatedGame = gameRepo.getGameById(joinedGameId)
        assertTrue(updatedGame!!.state == "player1_won")
    }

    @Test
    fun `in active game, player can play a move`() = runWithHandle { handle ->
        // given: a GameRepository, 2 users and a game type
        val gameRepo = JdbiGameRepository(handle)
        val user = createATestUser(JdbiUserRepository(handle))
        val gameTypeId = gameRepo.getGameTypeIdByVariantOpeningAndBoardSize(
            GameVariant("freestyle simple"),
            GameOpening("open"),
            BoardSize(15)
        )
        gameRepo.addGameToLobby(user.id, gameTypeId, Clock.System.now())
        val partner = createATestUser(JdbiUserRepository(handle))
        val boardUserData1 = BoardUserData(user.id, "b", null)
        val boardUserData2 = BoardUserData(partner.id, "w", null)
        val board = Board(15, boardUserData1, boardUserData2)
        val now = Clock.System.now()
        val gameRepresentation = Game(null, gameTypeId, board, user.id, partner.id, 1, "in-progress", now, now)
        val joinedGameId = gameRepo.createGame(gameRepresentation)

        // when: retrieving an active game
        val game = gameRepo.getGameById(joinedGameId)

        // then: the game is retrieved
        assertNotNull(game)

        // when: player plays a move
        val updatedBoard = board.addCoords(game.currentPlayer, now.epochSeconds, 0, 0)
        val updatedGame = game.copy(board = updatedBoard)
        gameRepo.updateGameBoard(updatedGame, updatedGame.id!!, 2, now.epochSeconds)

        // then: the game is updated
        val retrievedGame = gameRepo.getGameById(joinedGameId)
        assertTrue(retrievedGame!!.board.player1.coords!!.size == 1)
    }

    companion object {
        private fun createATestUser(userRepo: JdbiUserRepository): User {
            val userName = newTestUserName()
            val passwordValidationInfo = PasswordValidationInfo(newTokenValidationData())
            userRepo.storeUser(userName, passwordValidationInfo)

            return userRepo.getUserByUsername(userName)!!
        }
        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                // setURL("jdbc:postgresql://minedust.ddns.net:5432/gomokunew?user=postgres&password=4w5_Yd4xee35$")
            }
        ).configureWithAppRequirements()
    }
}
