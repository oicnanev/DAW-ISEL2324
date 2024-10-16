package isel.leic.daw.gomoku.domain.entities

data class BoardSize(
    val size: Int
) {
    init {
        require(size == 15 || size == 19) { "board size must be 15 or 19" }
    }
}
