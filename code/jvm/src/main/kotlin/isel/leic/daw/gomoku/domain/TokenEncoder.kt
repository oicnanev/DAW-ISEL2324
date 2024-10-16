package isel.leic.daw.gomoku.domain

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
