package isel.leic.daw.gomoku.repository

import isel.leic.daw.gomoku.domain.entities.About

interface SystemInfoRepository {
    fun getAbout(): About
}
