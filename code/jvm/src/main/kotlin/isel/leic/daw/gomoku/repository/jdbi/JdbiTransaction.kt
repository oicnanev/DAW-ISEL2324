package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.repository.GameRepository
import isel.leic.daw.gomoku.repository.SystemInfoRepository
import isel.leic.daw.gomoku.repository.Transaction
import isel.leic.daw.gomoku.repository.UserRankingRepository
import isel.leic.daw.gomoku.repository.UserRepository
import org.jdbi.v3.core.Handle

class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val userRepository: UserRepository = JdbiUserRepository(handle)
    override val gameRepository: GameRepository = JdbiGameRepository(handle)
    override val systemInfoRepository: SystemInfoRepository = JdbiSystemInfoRepository(handle)
    override val userRankingRepository: UserRankingRepository = JdbiRankingsRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}
