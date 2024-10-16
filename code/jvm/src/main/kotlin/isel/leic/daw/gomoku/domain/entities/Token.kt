package isel.leic.daw.gomoku.domain.entities

import isel.leic.daw.gomoku.domain.TokenValidationInfo
import kotlinx.datetime.Instant

data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)
