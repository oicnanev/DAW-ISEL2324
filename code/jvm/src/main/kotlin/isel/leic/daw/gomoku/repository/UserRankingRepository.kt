package isel.leic.daw.gomoku.repository

import isel.leic.daw.gomoku.domain.entities.Rank

interface UserRankingRepository {
    fun getRankings(): MutableList<Rank>?
}
