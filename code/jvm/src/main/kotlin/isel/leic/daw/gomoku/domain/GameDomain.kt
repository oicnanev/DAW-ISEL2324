package isel.leic.daw.gomoku.domain

import isel.leic.daw.gomoku.domain.entities.Board
import isel.leic.daw.gomoku.domain.entities.BoardSize
import isel.leic.daw.gomoku.domain.entities.GameOpening
import isel.leic.daw.gomoku.domain.entities.GameVariant
import isel.leic.daw.gomoku.domain.gameRules.FreestyleSimple
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameDomain {
    fun tryGameVariant(variant: String): GameVariant? {
        return try {
            GameVariant(variant)
        } catch (e: IllegalArgumentException) {
            println("error in GameVariant")
            println(e.message)
            null
        }
    }

    fun tryGameOpening(opening: String): GameOpening? {
        return try {
            GameOpening(opening)
        } catch (e: IllegalArgumentException) {
            println("error in GameOpening")
            println(e.message)
            null
        }
    }

    fun tryBoardSize(boardSize: Int): BoardSize? {
        return try {
            BoardSize(boardSize)
        } catch (e: IllegalArgumentException) {
            println("error in BoardSize")
            println(e.message)
            null
        }
    }

    fun getFirstPlayer(): Int =
        Random.nextInt(1, 3) // Generates a random number between 1 (inclusive) and 3 (exclusive)

    fun checkTurn(currentPlayer: Int, userID: Int, user1: Int): Boolean {
        return if (currentPlayer == 1) {
            (userID == user1)
        } else {
            (userID != user1)
        }
    }

    fun checkPosition(variant: String, opening: String, board: Board, x: Int, y: Int, player: Int): Boolean {
        // TODO: when the other variants are implemented, this function should have a when
        val game = FreestyleSimple(board, x, y, player)
        return game.isCoordinateValid(x, y)
    }

    fun checkVictory(variant: String, board: Board, player: Int): Boolean {
        // FreestyleSimple needs x and y to calculate the board position in insert coordinate
        // but no to check victory, so noX = -1 and noY = -1
        val noX = -1
        val noY = -1
        val game = FreestyleSimple(board, noX, noY, player)
        return game.checkVictory()
    }

    fun updateBoard(board: Board, x: Int, y: Int, currentPlayer: Int, updatedAt: Instant): Board {
        if (currentPlayer == 1) {
            board.addCoords(1, updatedAt.epochSeconds, x, y)
        } else {
            board.addCoords(2, updatedAt.epochSeconds, x, y)
        }
        return board
    }

    fun checkDraw(board: Board): Boolean {
        val boardSize = board.size * board.size

        val player1Coords = board.player1.coords?.let { listOf(it) } ?: emptyList()
        val player2Coords = board.player2.coords?.let { listOf(it) } ?: emptyList()

        if (player1Coords.isEmpty()) {
            return false
        }
        if (player2Coords.isEmpty()) {
            return false
        }
        val totalOccupiedCoords = player1Coords[0].size + player2Coords[0].size

        return totalOccupiedCoords == boardSize
    }
}
