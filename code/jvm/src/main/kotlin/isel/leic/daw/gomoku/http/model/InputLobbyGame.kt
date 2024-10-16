package isel.leic.daw.gomoku.http.model

data class InputLobbyGame(
    val variant: String,
    val opening: String,
    val boardSize: Int
)
