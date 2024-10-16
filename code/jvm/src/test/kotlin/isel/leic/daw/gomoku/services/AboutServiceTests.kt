package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.repository.jdbi.JdbiTransactionManager
import isel.leic.daw.gomoku.repository.jdbi.configureWithAppRequirements
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertTrue

class AboutServiceTests {
    @Test
    fun `can get about (authors and version)`() {
        // given: an AboutService
        val service = createAboutService()

        // when: getting about
        val about = service.getAbout()

        // then: the about is not null
        assertTrue { about.authors.size == 3 }
        assertTrue { about.version.version > 0.toString() }
    }

    companion object {
        private fun createAboutService(): AboutService {
            return AboutService(JdbiTransactionManager(jdbi))
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
