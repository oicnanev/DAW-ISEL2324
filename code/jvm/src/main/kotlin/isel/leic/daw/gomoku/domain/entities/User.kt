package isel.leic.daw.gomoku.domain.entities

import isel.leic.daw.gomoku.domain.PasswordValidationInfo

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val rank: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0
)
