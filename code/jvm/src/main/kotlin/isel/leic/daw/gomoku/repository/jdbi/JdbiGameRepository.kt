package isel.leic.daw.gomoku.repository.jdbi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import isel.leic.daw.gomoku.domain.entities.Board
import isel.leic.daw.gomoku.domain.entities.BoardSize
import isel.leic.daw.gomoku.domain.entities.Game
import isel.leic.daw.gomoku.domain.entities.GameOpening
import isel.leic.daw.gomoku.domain.entities.GameVariant
import isel.leic.daw.gomoku.http.model.OutputLobbyGame
import isel.leic.daw.gomoku.repository.GameRepository
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

class JdbiGameRepository(
    private val handle: Handle
) : GameRepository {
    override fun listLobbyGames(): List<OutputLobbyGame>? {
        return handle.createQuery(
            """
                SELECT lobby.id, variant, opening, board_size, username, rank
                FROM dbo.lobby
                JOIN dbo.types ON types.id = lobby.type_id
                JOIN dbo.users ON lobby.user_id = users.id
                WHERE active = true
            """.trimIndent()
        )
            .mapTo<OutputLobbyGame>()
            .list()
    }

    override fun hasUserAnActiveGameInLobby(userId: Int): Boolean {
        return handle.createQuery(
            "select count(*) from dbo.lobby where user_id = :userId and active = true"
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .single() == 1
    }

    override fun getGameTypeIdByVariantOpeningAndBoardSize(
        variant: GameVariant,
        opening: GameOpening,
        boardSize: BoardSize
    ): Int {
        return handle.createQuery(
            """
            select id from dbo.types 
            where variant = :variant and opening = :opening and board_size = :boardSize;
            """.trimIndent()
        )
            .bind("variant", variant.name)
            .bind("opening", opening.name)
            .bind("boardSize", boardSize.size)
            .mapTo<Int>()
            .one()
    }

    override fun addGameToLobby(userId: Int, gameType: Int, now: Instant): Int {
        return handle.createUpdate(
            """
            insert into dbo.lobby (user_id, type_id, created_at) values (:userId, :gameType, :createdAt)
            """
        )
            .bind("userId", userId)
            .bind("gameType", gameType)
            .bind("createdAt", now.epochSeconds)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    override fun isLobbyGameActive(id: Int): Boolean {
        return handle.createQuery("select count(*) from dbo.lobby where id = :id and active = true")
            .bind("id", id)
            .mapTo<Int>()
            .single() == 1
    }

    override fun getLobbyGameById(id: Int): OutputLobbyGame? {
        return handle.createQuery(
            """
            select lobby.id, username, rank, variant, opening, board_size from dbo.lobby
            join dbo.types on types.id = lobby.type_id
            join dbo.users on lobby.user_id = users.id
            where lobby.id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<OutputLobbyGame>()
            .single()
    }

    override fun createGame(game: Game): Int {
        logger.info("Creating game with gametypeid: ${game.gameTypeId}")

        // Create an ObjectMapper
        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        // Convert the game.board to a JSON string
        val boardJson = objectMapper.writeValueAsString(game.board)

        return handle.createUpdate(
            """
            insert into dbo.game (board, type_id, current_player, player1_id, player2_id, state, created_at, updated_at) 
            values (:board::jsonb, :game_type, :current_player, :player1, :player2, :state, :start_time, :updated)
            """.trimIndent()
        )
            .bind("board", boardJson)
            .bind("game_type", game.gameTypeId)
            .bind("current_player", game.currentPlayer)
            .bind("player1", game.player1)
            .bind("player2", game.player2)
            .bind("state", game.state)
            .bind("start_time", game.startTime.epochSeconds)
            .bind("updated", game.updated.epochSeconds)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    override fun deactivateLobbyGame(id: Int) {
        handle.createUpdate("update dbo.lobby set active = false where id = :id")
            .bind("id", id)
            .execute()
    }

    override fun getGameById(id: Int): Game? {
        return handle.createQuery("select * from dbo.game where id = :id")
            .bind("id", id)
            .mapTo<GameModel>()
            .singleOrNull()
            ?.gameModel
    }

    override fun getInProgressGameIdByPlayer1Id(id: Int): Int? =
        handle.createQuery("select id from dbo.game where player1_id = :id and state like 'in-progress'")
            .bind("id", id)
            .mapTo<Int>()
            .singleOrNull()

    override fun getOpeningByGameTypeId(gameTypeId: Int): String {
        return handle.createQuery("select opening from dbo.types where id = :gameTypeId")
            .bind("gameTypeId", gameTypeId)
            .mapTo<String>()
            .single()
    }

    override fun getVariantByGameTypeId(gameTypeId: Int): String {
        return handle.createQuery("select variant from dbo.types where id = :gameTypeId")
            .bind("gameTypeId", gameTypeId)
            .mapTo<String>()
            .single()
    }

    override fun updateGameBoard(newGame: Game, gameId: Int, newCurrentPlayer: Int, updatedAt: Long) {
        logger.info("Updating game with id: $gameId")

        // Create an ObjectMapper
        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        // Convert the game.board to a JSON string
        val boardJson = objectMapper.writeValueAsString(newGame.board)
        handle.createUpdate(

            """
            update dbo.game
            set board = :board::jsonb, current_player = :newCurrentPlayer, updated_at = :updatedAt 
            where id = :id
            """.trimMargin()
        )
            .bind("id", gameId)
            .bind("board", boardJson)
            .bind("newCurrentPlayer", newCurrentPlayer)
            .bind("updatedAt", updatedAt)
            .execute()
    }

    override fun updateGameState(newGame: Game, gameId: Int, updatedAt: Long) {
        logger.info("Updating game with id: $gameId")
        handle.createUpdate(
            """
            update dbo.game
            set state = :state, updated_at = :updatedAt 
            where id = :id
            """.trimMargin()
        )
            .bind("id", gameId)
            .bind("state", newGame.state)
            .bind("updatedAt", updatedAt)
            .execute()
    }

    override fun getInProgressGameByPlayerId(playerId: Int): Game? {
        return handle.createQuery(
            """
            select * from dbo.game 
            where (player1_id = :playerId or player2_id = :playerId) and state like 'in-progress'
            """.trimMargin()
        )
            .bind("playerId", playerId)
            .mapTo<GameModel>()
            .singleOrNull()
            ?.gameModel
    }

    private data class GameModel(
        val id: Int,
        val type_id: Int,
        val board: String,
        val player1_id: Int,
        val player2_id: Int,
        val current_player: Int,
        val state: String,
        val created_at: Long,
        val updated_at: Long
    ) {
        val gameModel: Game
            get() {
                val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
                // Convert the boardJson back into a Board object
                val board = objectMapper.readValue(board, Board::class.java)

                return Game(
                    id,
                    type_id,
                    board,
                    player1_id,
                    player2_id,
                    current_player,
                    state,
                    Instant.fromEpochSeconds(created_at),
                    Instant.fromEpochSeconds(updated_at)
                )
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JdbiGameRepository::class.java)
    }
}
