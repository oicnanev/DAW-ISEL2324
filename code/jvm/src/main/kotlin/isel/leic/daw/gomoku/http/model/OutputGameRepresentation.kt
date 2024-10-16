package isel.leic.daw.gomoku.http.model

import isel.leic.daw.gomoku.domain.entities.Board

data class OutputGameRepresentation(
    val id: Int,
    val board: Board,
    val opening: String,
    val variant: String,
    val currentPlayer: Int,
    val player1: String,
    val player2: String,
    val gameState: String,
    val startTime: Long,
    val lastUpdated: Long
)
