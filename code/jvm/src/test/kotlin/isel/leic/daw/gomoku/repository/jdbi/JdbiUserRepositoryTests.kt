package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.TestClock
import isel.leic.daw.gomoku.domain.PasswordValidationInfo
import isel.leic.daw.gomoku.domain.TokenValidationInfo
import isel.leic.daw.gomoku.domain.entities.Token
import isel.leic.daw.gomoku.domain.entities.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.DefaultAsserter.fail

// Don't forget to ensure DBMS is up (e.g. by running ./gradlew dbTestsWait)
class JdbiUserRepositoryTests {

    @Test
    fun `can create and retrieve user`() = runWithHandle { handle ->
        // given: a UsersRepository
        val repo = JdbiUserRepository(handle)

        // when: storing a user
        val userName = newTestUserName()
        val passwordValidationInfo = PasswordValidationInfo(newTokenValidationData())
        repo.storeUser(userName, passwordValidationInfo)

        // and: retrieving a user
        val retrievedUser: User? = repo.getUserByUsername(userName)

        // then:
        assertNotNull(retrievedUser)
        checkNotNull(retrievedUser)
        assertEquals(userName, retrievedUser.username)
        assertEquals(passwordValidationInfo.validationInfo, retrievedUser.passwordValidation.validationInfo)
        assertTrue(retrievedUser.id >= 0)

        // when: asking if the user exists
        val isUserIsStored = repo.isUserStoredByUsername(userName)

        // then: response is true
        assertTrue(isUserIsStored)

        // when: asking if a different user exists
        val anotherUserIsStored = repo.isUserStoredByUsername("another-$userName")

        // then: response is false
        assertFalse(anotherUserIsStored)
    }

    @Test
    fun `can create and validate tokens`() = runWithHandle { handle ->
        // given: a UsersRepository
        val repo = JdbiUserRepository(handle)
        // and: a test clock
        val clock = TestClock()

        // and: a createdUser
        val userName = newTestUserName()
        val passwordValidationInfo = PasswordValidationInfo("Pa55!$=?")
        val userId = repo.storeUser(userName, passwordValidationInfo)

        // and: test TokenValidationInfo
        val testTokenValidationInfo = TokenValidationInfo(newTokenValidationData())

        // when: creating a token
        val tokenCreationInstant = clock.now()
        val token = Token(
            testTokenValidationInfo,
            userId,
            createdAt = tokenCreationInstant,
            lastUsedAt = tokenCreationInstant
        )
        repo.createToken(token, 1)

        // then: createToken does not throw errors
        // no exception

        // when: retrieving the token and associated user
        val userAndToken = repo.getTokenByTokenValidationInfo(testTokenValidationInfo)
        // then:
        val (user, retrievedToken) = userAndToken ?: fail("token and associated user must exist")

        // and: ...
        assertEquals(userName, user.username)
        assertEquals(testTokenValidationInfo.validationInfo, retrievedToken.tokenValidationInfo.validationInfo)
        assertEquals(tokenCreationInstant, retrievedToken.createdAt)
    }

    companion object {

        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                // setURL("jdbc:postgresql://minedust.ddns.net:5432/gomokunew?user=postgres&password=4w5_Yd4xee35$")
            }
        ).configureWithAppRequirements()
    }
}
