package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.repository.jdbi.JdbiTransactionManager
import isel.leic.daw.gomoku.repository.jdbi.configureWithAppRequirements
import junit.framework.TestCase
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertTrue

class StatsServiceTests {
    @Test
    fun `can get users rankings`() {
        // given: a SystemInfoRepository
        val service = createStatsService()

        // when: getting about
        val stats = service.getRankings()

        // then: rankings are not null
        assertTrue(stats!!.size > 0)
        stats.forEach { TestCase.assertNotNull(it) }
    }

    companion object {
        private fun createStatsService(): StatsService {
            return StatsService(JdbiTransactionManager(jdbi))
        }

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
            }
        ).configureWithAppRequirements()

        // If used without the docker-compose.yml file, the following line should be uncommented:
        /* private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://minedust.ddns.net:5432/gomokunew?user=postgres&password=4w5_Yd4xee35$")
            }
        ).configureWithAppRequirements()
         */
    }
}
