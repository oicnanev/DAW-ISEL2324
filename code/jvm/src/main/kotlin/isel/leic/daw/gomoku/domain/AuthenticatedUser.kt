package isel.leic.daw.gomoku.domain

import isel.leic.daw.gomoku.domain.entities.User

class AuthenticatedUser(
    val user: User,
    val token: String
)
