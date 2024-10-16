package isel.leic.daw.gomoku.domain.entities

import kotlinx.datetime.Instant

data class Game(
    val id: Int?,
    val gameTypeId: Int,
    val board: Board,
    val player1: Int,
    val player2: Int,
    val currentPlayer: Int,
    val state: String,
    val startTime: Instant,
    val updated: Instant
) {
    init {
        val allowedStates = listOf("in-progress", "player1_won", "player2_won", "draw", "timed-out")
        require(currentPlayer == 1 || currentPlayer == 2) { "currentPlayer must be 1 or 2" }
        require(state in allowedStates) { "state must be one of $allowedStates" }
    }
}
