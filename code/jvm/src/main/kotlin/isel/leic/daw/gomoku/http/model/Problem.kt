package isel.leic.daw.gomoku.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        const val GIT_URI = "https://github.com/isel-leic-daw/2023-daw-leic51n-2023-daw-leic51n-g01/tree/main/code/jvm/"
        const val DOCS_URI = "docs/problems/"

        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(URI("${GIT_URI}${DOCS_URI}user-already-exists"))
        val insecurePassword = Problem(URI("${GIT_URI}${DOCS_URI}insecure-password"))
        val userOrPasswordAreInvalid = Problem(URI("${GIT_URI}${DOCS_URI}user-or-password-are-invalid"))
        val userNotFound = Problem(URI("${GIT_URI}${DOCS_URI}user-not-found"))
        val invalidRequestContent = Problem(URI("${GIT_URI}${DOCS_URI}invalid-request-content"))
        val gameAlreadyInLobby = Problem(URI("${GIT_URI}${DOCS_URI}game-already-enqueued"))
        val invalidBoardSize = Problem(URI("${GIT_URI}${DOCS_URI}invalid-board"))
        val invalidOpening = Problem(URI("${GIT_URI}${DOCS_URI}invalid-opening"))
        val invalidVariant = Problem(URI("${GIT_URI}${DOCS_URI}invalid-variant"))
        val waitForPartner = Problem(URI("${GIT_URI}${DOCS_URI}waiting-for-partner"))
        val userAlreadyHasGameInLobby = Problem(URI("${GIT_URI}${DOCS_URI}user-already-has-game-in-lobby"))
        val invalidMove = Problem(URI("${GIT_URI}${DOCS_URI}invalid-move"))
        val invalidPlayer = Problem(URI("${GIT_URI}${DOCS_URI}invalid-player"))
        val invalidPosition = Problem(URI("${GIT_URI}${DOCS_URI}invalid-position"))
        val invalidToken = Problem(URI("${GIT_URI}${DOCS_URI}invalid-token"))
        val invalidGame = Problem(URI("${GIT_URI}${DOCS_URI}invalid-game"))
    }
}
