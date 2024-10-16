package isel.leic.daw.gomoku.domain.entities

data class Rank(
    val id: Int,
    val username: String,
    val rank: Int,
    val variant: String,
    val opening: String,
    val gamesPlayed: Int,
    val wins: Int,
    val draws: Int
)
