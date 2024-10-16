package isel.leic.daw.gomoku.http.model

class StatsOutputModel(
    val rankings: MutableList<RankOutputModel>
)

class RankOutputModel(
    val username: String,
    val rank: Int,
    val variant: String,
    val opening: String,
    val gamesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val link: String
)
