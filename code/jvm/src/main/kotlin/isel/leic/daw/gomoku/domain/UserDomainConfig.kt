package isel.leic.daw.gomoku.domain

import kotlin.time.Duration

data class UserDomainConfig(
    val tokenSizeInBytes: Int,
    val tokenTtl: Duration,
    val tokenRollingTtl: Duration,
    val maxTokensPerUser: Int
) {
    init {
        require(tokenSizeInBytes > 0) { "tokenSizeInBytes must be positive" }
        require(tokenTtl.isPositive()) { "tokenTtl must be positive" }
        require(tokenRollingTtl.isPositive()) { "tokenRollinTtl must be positive" }
        require(maxTokensPerUser > 0) { "maxTokensPerUser must be positive" }
    }
}
