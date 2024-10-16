package isel.leic.daw.gomoku.http.model

private const val baseUri = "http://localhost:8080/api"
class OutputAPIDescription {
    val class_ = listOf("gomoku")
    val properties = GomokuPropertiesModel()
    val entities = listOf(
        GamePropertiesModel(),
        UserPropertiesModel(),
        RankingsPropertiesModel()
    )
    val actions = listOf(
        ActionPropertiesModel(
            "register-user",
            "Register",
            "POST",
            "$baseUri/users",
            "item",
            "application/json",
            listOf(
                FieldPropertiesModel(
                    "username",
                    "text"
                ),
                FieldPropertiesModel(
                    "password",
                    "password",
                    "must have at least 8 characters, 1 digit, 1 uppercase letter, 1 lowercase letter and 1 special character"
                )
            )
        ),
        ActionPropertiesModel(
            "login",
            "Login",
            "POST",
            "$baseUri/users/token",
            "item",
            "application/json",
            listOf(
                FieldPropertiesModel(
                    "username",
                    "text"
                ),
                FieldPropertiesModel(
                    "password",
                    "password"
                )
            )
        ),
        ActionPropertiesModel(
            "logout",
            "Logout",
            "POST",
            "$baseUri/logout",
            "item"
        ),
        ActionPropertiesModel(
            "user-home",
            "Home",
            "GET",
            "$baseUri/me",
            "item",
            "application/json",
            listOf(
                FieldPropertiesModel(
                    "username",
                    "application/json"
                ),
                FieldPropertiesModel(
                    "stats",
                    "application/json"
                )
            )
        ),
        ActionPropertiesModel(
            "game",
            "Play game",
            "POST",
            "$baseUri/game",
            "item",
            "application/json",
            listOf(
                FieldPropertiesModel(
                    "username",
                    "hidden"
                ),
                FieldPropertiesModel(
                    "password",
                    "hidden"
                ),
                FieldPropertiesModel(
                    "variant",
                    "text"
                ),
                FieldPropertiesModel(
                    "opening",
                    "text"
                ),
                FieldPropertiesModel(
                    "board_size",
                    "numeric"
                ),
                FieldPropertiesModel(
                    "opponent",
                    "numeric"
                )
            )
        )
    )
    val links = listOf(
        LinkPropertiesModel(
            "collection",
            baseUri
        )
    )
}

class RankingsPropertiesModel {
    val class_ = listOf("Rankings")
    val properties = RankingsClassPropertiesModel()
    val links = listOf(
        LinkPropertiesModel(
            "self",
            "$baseUri/rankings"
        ),
        LinkPropertiesModel(
            "collection",
            "$baseUri/rankings"
        ),
        LinkPropertiesModel(
            "next",
            "$baseUri/rankings?offset=2&limit=2"
        ),
        LinkPropertiesModel(
            "prev",
            "$baseUri/rankings?offset=0&limit=2"
        ),
        LinkPropertiesModel(
            "first",
            "$baseUri/rankings?offset=0&limit=2"
        )
    )
}

class RankingsClassPropertiesModel {
    val rankings = listOf(
        """
        {
            "class": ["Ranking"],
            "properties": {
                "username": "John Smith",
                "games": 111,
                "victories": 100,
                "defeats": 10,
                "draws": 1,
                "rank": 301
            }
        }
        """.trimIndent().replace("\n", ""),
        """
        {
            "class": ["Ranking"],
            "properties": {
                "username": "Anne Chow",
                "games": 91,
                "victories": 91,
                "defeats": 0,
                "draws": 0,
                "rank": 273
            }
        }
        """.trimIndent().replace("\n", "")
    )
}

class GamePropertiesModel {
    val class_ = listOf("Game")
    val properties = GameClassPropertiesModel()
    val links = listOf(
        LinkPropertiesModel(
            "self",
            "$baseUri/game"
        ),
        LinkPropertiesModel(
            "item",
            "$baseUri/lobby"
        ),
        LinkPropertiesModel(
            "item",
            "$baseUri/joingame"
        ),
        LinkPropertiesModel(
            "item",
            "$baseUri/checkmatch"
        ),
        LinkPropertiesModel(
            "item",
            "$baseUri/game/{id}"
        )
    )
}

class GameClassPropertiesModel {
    val id = 1
    val gameType = 2
    val board = BoardPropertiesModel()
    val player1Id = "123"
    val player2Id = "321"
    val currentPlayer = 1
    val state = "in-progress"
    val createdAt = 10000
    val updatedAt = 10010
}

class UserPropertiesModel {
    val class_ = listOf("User", "collection")
    val properties = UserClassPropertiesModel()
    val links = listOf(
        LinkPropertiesModel(
            "self",
            "$baseUri/users"
        ),
        LinkPropertiesModel(
            "related",
            "$baseUri/users/token"
        ),
        LinkPropertiesModel(
            "related",
            "$baseUri/logout"
        ),
        LinkPropertiesModel(
            "me",
            "$baseUri/me"
        ),
        LinkPropertiesModel(
            "related",
            "$baseUri/users/{id}"
        )
    )
}

class UserClassPropertiesModel {
    val id = 1
    val username = "John Smith"
    val passwordValidationInfo = "{token}"
}

data class LinkPropertiesModel(
    val rel: String,
    val href: String
)

data class ActionPropertiesModel(
    val name: String,
    val title: String,
    val method: String,
    val href: String,
    val rel: String,
    val type: String? = null,
    val fields: List<FieldPropertiesModel>? = null
)

data class FieldPropertiesModel(
    val name: String,
    val type: String,
    val value: String? = null
)

class GomokuPropertiesModel {
    val user = "User"
    val game = "Game"
    val rankings = "Rankings"
}

class BoardPropertiesModel {
    val size: Int = 15
    val player1 = PlayerBoardPropertiesModel()
    val player2 = PlayerBoardPropertiesModel()
}

class PlayerBoardPropertiesModel {
    val piece = "b"
    val coords = listOf(CoordsPropertiesModel())
}

class CoordsPropertiesModel {
    val x: Int = 1
    val y: Int = 0
    val timestamp: Long = 876123773123
}
