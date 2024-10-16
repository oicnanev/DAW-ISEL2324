package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.domain.entities.Rank
import isel.leic.daw.gomoku.repository.TransactionManager
import org.springframework.stereotype.Component

@Component
class StatsService(
    private val transactionManager: TransactionManager
) {
    fun getRankings(): MutableList<Rank>? {
        return transactionManager.run {
            val statsRepository = it.userRankingRepository
            statsRepository.getRankings()
        }
    }
}
