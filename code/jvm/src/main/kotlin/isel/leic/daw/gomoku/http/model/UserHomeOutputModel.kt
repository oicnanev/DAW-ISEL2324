package isel.leic.daw.gomoku.http.model

data class UserHomeOutputModel(
    val id: Int,
    val username: String,
    val rank: Int,
    val gamesPlayed: Int,
    val wins: Int,
    val draws: Int
)
