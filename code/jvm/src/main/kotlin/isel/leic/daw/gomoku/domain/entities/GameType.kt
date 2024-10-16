package isel.leic.daw.gomoku.domain.entities

data class GameType(
    val gameVariant: GameVariant,
    val gameOpening: GameOpening,
    val boardSize: BoardSize
)
