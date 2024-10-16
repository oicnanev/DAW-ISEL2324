package isel.leic.daw.gomoku.repository.jdbi

import junit.framework.TestCase
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource

class JdbiRankingsRepositoryTest {
    @Test
    fun `can get users rankings`() = runWithHandle { handle ->
        // given: a SystemInfoRepository
        val repo = JdbiRankingsRepository(handle)

        // when: getting about
        val ranks = repo.getRankings()

        // then:
        TestCase.assertNotNull(ranks)
        ranks?.forEach { TestCase.assertNotNull(it) }
    }

    companion object {

        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                // setURL("jdbc:postgresql://minedust.ddns.net:5432/gomokunew?user=postgres&password=4w5_Yd4xee35$")
            }
        ).configureWithAppRequirements()
    }
}
