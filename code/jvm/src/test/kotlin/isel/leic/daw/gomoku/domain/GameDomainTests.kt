package isel.leic.daw.gomoku.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameDomainTests {

    // given: an HTTP client
    // and: a random user
    // when: creating an user
    // then: the response is a 201 with a proper Location header

    private val game = GameDomain()

    @Test
    fun testCorrectGameVariant() {
        // given: a correct game variant name
        val temp = "freestyle simple"

        // when: Assessing if the given value is correct
        val midTemp = game.tryGameVariant(temp)

        // then: a freestyle symple variant should have been created
        assertNotNull(midTemp)
        assertEquals(midTemp.name, temp)
    }

    @Test
    fun testIncorrectGameVariant() {
        // given: an Incorrect game variant name
        val temp = "NonExistent Variant"

        // when: Assessing if the given value is correct
        val midTemp = game.tryGameVariant(temp)

        // then: variant should be null
        assertNull(midTemp)
    }

    @Test
    fun testCorrectGameOpening() {
        // given: a correct game opening name
        val temp = "open"

        // when: Assessing if the given value is correct
        val midTemp = game.tryGameOpening(temp)

        // then: an open Opening should have been created
        assertNotNull(midTemp)
        assertEquals(midTemp.name, temp)
    }

    @Test
    fun testIncorrectOpening() {
        // given: an Incorrect game variant name
        val temp = "NonExistent Opening"

        // when: Assessing if the given value is correct
        val midTemp = game.tryGameOpening(temp)

        // then: opening should be null
        assertNull(midTemp)
    }

    @Test
    fun testCorrectBoardSize() {
        // given: a correct board size
        val temp = 15

        // when: Assessing if the given value is correct
        val midTemp = game.tryBoardSize(temp)

        // then: a BoardSize should have been created
        assertNotNull(midTemp)
        assertEquals(midTemp.size, temp)
    }

    @Test
    fun testIncorrectBoardSize() {
        // given: an Incorrect Board Size value
        val temp = 666

        // when: Assessing if the given value is correct
        val midTemp = game.tryBoardSize(temp)

        // then: boardsize should be null
        assertNull(midTemp)
    }

    @Test
    fun testGetFirstPlayer() {
        var temp: Int
        // running 100 test
        for (i in 1..100) {
            // when: getting a player number
            temp = game.getFirstPlayer()
            // then: first player must be 1 or 2
            assertTrue { temp == 1 || temp == 2 }
        }
    }
}
