package isel.leic.daw.gomoku.domain.gameRules

interface GameRule {
    fun checkVictory(): Boolean

    fun checkHorizontal(matrixBoard: MutableList<MutableList<String>>): Boolean

    fun checkVertical(matrixBoard: MutableList<MutableList<String>>): Boolean

    fun checkDiagonals(matrixBoard: MutableList<MutableList<String>>): Boolean

    fun isCoordinateValid(x: Int, y: Int): Boolean
}
