package isel.leic.daw.gomoku.repository.jdbi

import junit.framework.TestCase
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource

class JdbiSystemInfoRepositoryTests {
    @Test
    fun `can get about (authors and version)`() = JdbiSystemInfoRepositoryTests.runWithHandle { handle ->
        // given: a SystemInfoRepository
        val repo = JdbiSystemInfoRepository(handle)

        // when: getting about
        val about = repo.getAbout()

        // then:
        TestCase.assertNotNull(about)
        about.authors.forEach { TestCase.assertNotNull(it) }
        TestCase.assertTrue(about.authors.size == 3)
        about.version.let { TestCase.assertNotNull(it) }
        TestCase.assertTrue(about.version.version > 0.toString())
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
