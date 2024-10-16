package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.http.model.OutputGameRepresentation
import isel.leic.daw.gomoku.http.model.OutputLobbyGamesListRepresentation
import isel.leic.daw.gomoku.http.model.TokenResponse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.math.abs
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {
    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @Test
    fun `can list lobby games`() {
        // given: an HTTP client and a authenticated user
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user = createUser(port)

        // when: listing lobby games
        // then: the response is a 200 with a proper LobbyGamesList body
        client.get().uri("/lobby")
            .header("Authorization", "Bearer ${user.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(OutputLobbyGamesListRepresentation::class.java)
    }

    @Test
    fun `can add game to lobby`() {
        // given: an HTTP client and a authenticated user
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user = createUser(port)

        // when: adding a game to lobby
        // then: the response is a 201 with a proper Location header
        client.post().uri("/lobby")
            .header("Authorization", "Bearer ${user.token}")
            .bodyValue(
                mapOf(
                    "variant" to "freestyle simple",
                    "opening" to "open",
                    "boardSize" to 15
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/lobby/"))
            }
    }

    @Test
    fun `can give up a created lobby game`() {
        // given: an HTTP client, a authenticated user and a created lobby game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user = createUser(port)
        val lobbyGameId = createLobbyGame(client, user)

        // when: giving up a created lobby game
        // then: the response is a 200
        client.delete().uri("/lobby/$lobbyGameId")
            .header("Authorization", "Bearer ${user.token}")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `can check lobby game status`() {
        // given: an HTTP client, a authenticated user and a created lobby game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user = createUser(port)
        createLobbyGame(client, user)

        // when: checking lobby game status
        // then: the response is a 200 with a proper LobbyGame body
        client.get().uri("/checkmatch")
            .header("Authorization", "Bearer ${user.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
    }

    @Test
    fun `can join a lobby game created by another user`() {
        // given: an HTTP client, two authenticated users and a created lobby game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user1 = createUser(port)
        val user2 = createUser(port)
        val lobbyGameId = createLobbyGame(client, user1)

        // when: joining a lobby game created by another user
        // then: the response is a 200 and the created game id is returned in location header
        client.post().uri("/joingame/$lobbyGameId")
            .header("Authorization", "Bearer ${user2.token}")
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/game/"))
            }
    }

    @Test
    fun `can view a created or played game`() {
        // given: an HTTP client, two authenticated users and a created game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user1 = createUser(port)
        val user2 = createUser(port)
        val gameId = createGame(client, user1, user2)

        // when: viewing a created or played game
        // then: the response is a 200 with a proper Game body
        client.get().uri("/game/$gameId")
            .header("Authorization", "Bearer ${user1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(OutputGameRepresentation::class.java)
    }

    @Test
    fun `can give up a game`() {
        // given: an HTTP client, two authenticated users and a created game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user1 = createUser(port)
        val user2 = createUser(port)
        createGame(client, user1, user2)

        // when: giving up a game
        // then: the response is a 200 with a proper Game body
        client.post().uri("/game/giveup")
            .header("Authorization", "Bearer ${user1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(OutputGameRepresentation::class.java)
    }

    @Test
    fun `can play a move`() {
        // given: an HTTP client, two authenticated users and a created game
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val user1 = createUser(port)
        val user2 = createUser(port)
        val gameId = createGame(client, user1, user2)
        val currentUser = getCurentUser(client, gameId, user1)

        // when: playing a move
        // then: the response is a 200 with a proper Game body
        if (currentUser == 1) {
            client.post().uri("/game/$gameId")
                .header("Authorization", "Bearer ${user1.token}")
                .bodyValue(
                    mapOf(
                        "x" to 1,
                        "y" to 1
                    )
                )
                .exchange()
                .expectStatus().isCreated
                .expectBody(OutputGameRepresentation::class.java)
        } else {
            client.post().uri("/game/$gameId")
                .header("Authorization", "Bearer ${user2.token}")
                .bodyValue(
                    mapOf(
                        "x" to 1,
                        "y" to 1
                    )
                )
                .exchange()
                .expectStatus().isCreated
                .expectBody(OutputGameRepresentation::class.java)
        }
    }

    companion object {
        private fun getCurentUser(client: WebTestClient, gameId: Int, user: AuthenticatedUser): Int {
            return client.get().uri("/game/$gameId")
                .header("Authorization", "Bearer ${user.token}")
                .exchange()
                .expectStatus().isOk
                .expectBody(OutputGameRepresentation::class.java)
                .returnResult()
                .responseBody!!
                .currentPlayer
        }

        private fun createGame(client: WebTestClient, user1: AuthenticatedUser, user2: AuthenticatedUser): Int {
            val lobbyGameId = createLobbyGame(client, user1)
            return client.post().uri("/joingame/$lobbyGameId")
                .header("Authorization", "Bearer ${user2.token}")
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("location") {
                    assertTrue(it.startsWith("/api/game/"))
                }
                .returnResult<String>()
                .responseHeaders
                .location
                .toString()
                .split("/")
                .last()
                .toInt()
        }

        private fun createLobbyGame(client: WebTestClient, authenticatedUser: AuthenticatedUser): Int {
            return client.post().uri("/lobby")
                .header("Authorization", "Bearer ${authenticatedUser.token}")
                .bodyValue(
                    mapOf<String, Any>(
                        "variant" to "freestyle simple",
                        "opening" to "open",
                        "boardSize" to 15
                    )
                )
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("location") {
                    assertTrue(it.startsWith("/api/lobby/"))
                }
                .returnResult<String>()
                .responseHeaders
                .location
                .toString()
                .split("/")
                .last()
                .toInt()
        }
        private fun createUser(port: Int): AuthenticatedUser {
            val client =
                WebTestClient
                    .bindToServer()
                    .baseUrl("http://localhost:$port/api")
                    .build()
            val username = newTestUserName()
            val password = "P455w0d?!"

            client.post().uri("/users")
                .bodyValue(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )
                .exchange()

            val result = client.post().uri("/users/token")
                .bodyValue(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody(TokenResponse::class.java)
                .returnResult()
                .responseBody!!

            return AuthenticatedUser(username, result.token)
        }
        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private data class AuthenticatedUser(
            val username: String,
            val token: String
        )
    }
}
