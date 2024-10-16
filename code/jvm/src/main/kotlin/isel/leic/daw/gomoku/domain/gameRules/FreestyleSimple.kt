package isel.leic.daw.gomoku.domain.gameRules

import isel.leic.daw.gomoku.domain.entities.Board

class FreestyleSimple(
    val board: Board,
    val x: Int,
    val y: Int,
    val player: Int
) : GameRule {
    override fun checkVictory(): Boolean {
        val playerCoords = if (player == 1) board.player1.coords else board.player2.coords
        val matrixBoard = MutableList<MutableList<String>>(board.size) { MutableList(board.size) { " " } }

        for (coords in playerCoords!!) matrixBoard[coords.x][coords.y] = "X"

        return if (
            checkHorizontal(matrixBoard) || checkVertical(matrixBoard) || checkDiagonals(matrixBoard)
        ) {
            true
        } else {
            false
        }
    }

    override fun checkHorizontal(matrixBoard: MutableList<MutableList<String>>): Boolean {
        for (i in 0 until board.size - 5) {
            for (j in 0 until board.size) {
                var consecutiveStones = 0
                for (k in 0 until 5) {
                    if (matrixBoard[i + k][j] == "X") consecutiveStones++
                }
                if (consecutiveStones == 5) return true
            }
        }
        return false
    }

    override fun checkVertical(matrixBoard: MutableList<MutableList<String>>): Boolean {
        for (i in 0 until board.size) {
            for (j in 0 until board.size - 5) {
                var consecutiveStones = 0
                for (k in 0 until 5) {
                    if (matrixBoard[i][j + k] == "X") consecutiveStones++
                }
                if (consecutiveStones == 5) return true
            }
        }
        return false
    }

    override fun checkDiagonals(matrixBoard: MutableList<MutableList<String>>): Boolean =
        checkTopLeftToBottomRight(matrixBoard) || checkTopRightToBottomLeft(matrixBoard)

    private fun checkTopLeftToBottomRight(matrixBoard: MutableList<MutableList<String>>): Boolean {
        for (i in 0 until board.size - 5) {
            for (j in 0 until board.size - 5) {
                var consecutiveStones = 0
                for (k in 0 until 5) {
                    if (matrixBoard[i + k][j + k] == "X") consecutiveStones++
                }
                if (consecutiveStones == 5) return true
            }
        }
        return false
    }

    private fun checkTopRightToBottomLeft(matrixBoard: MutableList<MutableList<String>>): Boolean {
        for (i in 4 until board.size) {
            for (j in 0 until board.size - 5) {
                var consecutiveStones = 0
                for (k in 0 until 5) {
                    if (matrixBoard[i - k][j + k] == "X") consecutiveStones++
                    // println("x: ${i - k} y: ${j + k} piece: ${matrixBoard[i - k][j + k]} consecutiveStones: $consecutiveStones")
                }
                if (consecutiveStones == 5) return true
            }
        }
        return false
    }

    override fun isCoordinateValid(x: Int, y: Int): Boolean {
        return when {
            x < 0 -> false
            y < 0 -> false
            x > board.size -> false
            y > board.size -> false
            isCoordinateOccupied(x, y) -> false
            else -> true
        }
    }

    private fun isCoordinateOccupied(x: Int, y: Int): Boolean {
        val opponentCoords = if (player == 1) board.player2.coords else board.player1.coords
        if (opponentCoords == null) {
            return false
        } else {
            for (coords in opponentCoords) {
                if (coords.x == x && coords.y == y) {
                    return true
                }
            }
            return false
        }
    }
}
