package isel.leic.daw.gomoku.repository

interface Transaction {
    val userRepository: UserRepository
    val gameRepository: GameRepository
    val systemInfoRepository: SystemInfoRepository
    val userRankingRepository: UserRankingRepository

    fun rollback()
}
