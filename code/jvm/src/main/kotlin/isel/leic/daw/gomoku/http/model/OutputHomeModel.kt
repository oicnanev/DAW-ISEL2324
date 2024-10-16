package isel.leic.daw.gomoku.http.model

class OutputHomeModel {
    val welcome = "Welcome to Gomoku API"
    val description =
        "This API allows you to play Gomoku, a strategy board game for two players, also known as Five in a Row. The objective of the game is to form a row of five consecutive pieces, horizontally, vertically or diagonally."
    val usage = LinkModel()
}

class LinkModel {
    val rel = listOf("collection")
    val href = "https://documenter.getpostman.com/view/24046057/2s9YRGyUc9"
}
