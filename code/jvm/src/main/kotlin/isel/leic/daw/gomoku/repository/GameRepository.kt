package isel.leic.daw.gomoku.repository

import isel.leic.daw.gomoku.domain.entities.BoardSize
import isel.leic.daw.gomoku.domain.entities.Game
import isel.leic.daw.gomoku.domain.entities.GameOpening
import isel.leic.daw.gomoku.domain.entities.GameVariant
import isel.leic.daw.gomoku.http.model.OutputLobbyGame
import kotlinx.datetime.Instant

interface GameRepository {
    fun listLobbyGames(): List<OutputLobbyGame>?

    fun hasUserAnActiveGameInLobby(userId: Int): Boolean

    fun getGameTypeIdByVariantOpeningAndBoardSize(
        variant: GameVariant,
        opening: GameOpening,
        boardSize: BoardSize
    ): Int

    fun addGameToLobby(userId: Int, gameType: Int, now: Instant): Int

    fun isLobbyGameActive(id: Int): Boolean

    fun getLobbyGameById(id: Int): OutputLobbyGame?

    fun createGame(game: Game): Int

    fun deactivateLobbyGame(id: Int)

    fun getGameById(id: Int): Game?

    fun getInProgressGameIdByPlayer1Id(id: Int): Int?

    fun getOpeningByGameTypeId(gameTypeId: Int): String

    fun getVariantByGameTypeId(gameTypeId: Int): String

    fun updateGameBoard(newGame: Game, gameId: Int, newCurrentPlayer: Int, updatedAt: Long)

    fun updateGameState(newGame: Game, gameId: Int, updatedAt: Long)

    fun getInProgressGameByPlayerId(playerId: Int): Game?
}
