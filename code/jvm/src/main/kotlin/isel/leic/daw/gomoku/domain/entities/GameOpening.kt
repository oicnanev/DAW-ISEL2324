package isel.leic.daw.gomoku.domain.entities

data class GameOpening(
    val name: String
) {
    init {
        val allowedNames = listOf("open", "swap", "swap2", "pro", "long_pro", "soosorv")
        require(name in allowedNames) { "name must be one of $allowedNames" }
    }
}
