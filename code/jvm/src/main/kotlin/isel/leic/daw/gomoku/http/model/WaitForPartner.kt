package isel.leic.daw.gomoku.http.model

data class WaitForPartner(
    val message: String = "No partner found yet. Retry-After header will be set to the number of seconds to wait for a partner",
    val uri: String = "http://localhost:8080/api/checkmatch"
)
