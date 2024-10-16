package isel.leic.daw.gomoku.domain.entities

data class UserStats(
    val id: Int?,
    val user: Int,
    val gameType: Int,
    val rank: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val gamesDraw: Int
) {
    init {
        require(user >= 0) { "user must be positive" }
        require(gameType >= 0) { "gameType must be positive" }
        require(rank >= 0) { "rank must be positive" }
        require(gamesPlayed >= 0) { "gamesPlayed must be positive" }
        require(gamesWon >= 0) { "gamesWon must be positive" }
        require(gamesDraw >= 0) { "gamesDraw must be positive" }
    }
}
