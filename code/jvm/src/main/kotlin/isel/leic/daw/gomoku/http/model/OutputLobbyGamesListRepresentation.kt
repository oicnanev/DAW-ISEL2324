package isel.leic.daw.gomoku.http.model

data class OutputLobbyGamesListRepresentation(
    val outputLobbyGameList: MutableList<OutputLobbyGame>
) {
    fun add(lobbyGame: OutputLobbyGame) = outputLobbyGameList.add(lobbyGame)
}
