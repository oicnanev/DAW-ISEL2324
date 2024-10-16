package isel.leic.daw.gomoku.http.model

data class OutputLobbyGame(
    var id: Int,
    var variant: String,
    var opening: String,
    val boardSize: Int,
    var username: String,
    var rank: Int
)
