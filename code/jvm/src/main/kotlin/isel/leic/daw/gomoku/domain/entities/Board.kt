package isel.leic.daw.gomoku.domain.entities

data class Board(
    val size: Int,
    val player1: BoardUserData,
    val player2: BoardUserData
) {
    fun addCoords(currentPlayer: Int, timestamp: Long, x: Int, y: Int): Board {
        if (currentPlayer == 1) {
            player1.add(timestamp, x, y)
        } else {
            player2.add(timestamp, x, y)
        }

        return this
    }
}

data class BoardUserData(
    val userId: Int,
    val stone: String,
    var coords: MutableList<BoardCoordData>?
) {
    fun add(timestamp: Long, x: Int, y: Int): MutableList<BoardCoordData>? {
        if (coords == null) {
            coords = mutableListOf()
        }
        coords?.add(BoardCoordData(timestamp, x, y))
        return coords
    }
}

data class BoardCoordData(
    val timestamp: Long,
    val x: Int,
    val y: Int
)
