package isel.leic.daw.gomoku.domain.entities

import kotlinx.datetime.Instant

data class GameMove(
    val id: String,
    val gameID: String,
    val playerID: Int,
    val x: Int,
    val y: Int,
    val createdAt: Instant
) {
    init {
        require(playerID == 1 || playerID == 2)
    }
}
