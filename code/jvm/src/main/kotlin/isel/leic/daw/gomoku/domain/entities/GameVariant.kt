package isel.leic.daw.gomoku.domain.entities

data class GameVariant(
    val name: String
) {
    init {
        val allowedNames = listOf(
            "freestyle simple",
            "freestyle swap after 1st move",
            "renju",
            "caro",
            "omok",
            "ninuki-renju",
            "pente"
        )
        require(name in allowedNames) { "name must be one of $allowedNames" }
    }
}
