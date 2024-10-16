package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.domain.entities.About
import isel.leic.daw.gomoku.repository.TransactionManager
import org.springframework.stereotype.Component

@Component
class AboutService(
    private val transactionManager: TransactionManager
) {
    fun getAbout(): About {
        return transactionManager.run {
            val systemInfoRepository = it.systemInfoRepository
            systemInfoRepository.getAbout()
        }
    }
}
