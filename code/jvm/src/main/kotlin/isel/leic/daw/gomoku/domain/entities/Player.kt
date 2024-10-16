package isel.leic.daw.gomoku.domain.entities

class Player {
    val black: String
        get() = "●" // unicode black circle "\u25CF"

    val white: String
        get() = "○" // unicode white circle "\u25CB"
}
