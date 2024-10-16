package isel.leic.daw.gomoku.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InfoTest {
    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @Test
    fun canGetInfo() {
        // given: an HTTP client
        val client =
            WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // when: getting home
        // then: the response is a 200 with a proper json body
        client.get().uri("/info")
            .exchange()
            .expectStatus().isOk
            .expectBody()
    }
}
