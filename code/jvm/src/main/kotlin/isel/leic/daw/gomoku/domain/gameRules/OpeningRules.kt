package isel.leic.daw.gomoku.domain.gameRules

/*
* Example of Board representation, the pieces go into the + symbol
* Player1 ● - black
* Player2 ○ - white
*
*    01 02 03 04 05 06 07 08 09 10 11 12 13 14 15
* 01 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 02 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 03 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 04 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 05 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 06 --+--+--+--+--+--+--+--○--+--+--+--+--+--+--
* 07 --+--+--+--+--+--+--●--+--+--+--+--+--+--+--
* 08 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 09 --+--+--+--+--+--+--+--+--●--+--+--+--+--+--
* 10 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 11 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 12 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 13 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 14 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
* 15 --+--+--+--+--+--+--+--+--+--+--+--+--+--+--
 */

class OpeningRules {
    private val IllegalMove = Throwable("Illegal Move")

    /* ****************************************************************************************************************
    * The first player's first stone must be placed in the center of the board.
    * The second player's first stone may be placed anywhere on the board.
    * The first player's second stone must be placed at least three intersections away from the first stone
    * (two empty intersections in between the two stones)     */
    fun verifyPro(playerNum: Int, moveNum: Int, boardSize: Int, x: Int, y: Int, typeOfPro: String = "simple"): Boolean {
        // typeOfPro could be simple or long
        return when (moveNum) {
            1 -> if (playerNum == 1) {
                verifyCenterSquare(boardSize, x, y, moveNum)
            } else {
                throw IllegalMove
            }
            2 -> if (playerNum == 2) {
                true
            } else {
                throw IllegalMove
            }
            3 -> if (playerNum == 1) {
                // TODO: Maybe instead of the boardSize we must pass the board to check if player don't play in
                // top of the opponent piece
                if (typeOfPro == "simple") {
                    verifyThreeIntersectionsAway(boardSize, x, y)
                } else {
                    verifyFourIntersectionsAway(boardSize, x, y)
                }
            } else {
                throw IllegalMove
            }
            else -> false
        }
    }

    /* ****************************************************************************************************************
    * The first player's first stone must be placed in the center of the board.
    * The second player's first stone may be placed anywhere on the board.
    * The first player's second stone must be placed at least four intersections away from the first stone
    * (three empty intersections in between the two stones)
    * */
    fun verifyLongPro(playerNum: Int, moveNum: Int, boardSize: Int, x: Int, y: Int, typeOfPro: String = "long"): Boolean {
        return verifyPro(playerNum, moveNum, boardSize, x, y, "long")
    }

    /* ****************************************************************************************************************
    * The tentative first player places three stones (two black, and one white) anywhere on the board.
    * The tentative second player then chooses which color to play as.
    * Play proceeds from there as normal with white playing their second stone.     */
    fun verifySwap(): Boolean {
        // TODO: implement
        return false
    }

    /* ****************************************************************************************************************
    * The tentative first player places three stones on the board, two black and one white.
    * The tentative second player then has three options:
    *   1. They can choose to play as white and place a second white stone
    *   2. They can swap their color and choose to play as black
    *   3. Or they can place two more stones, one black and one white, and pass the choice of which color to play back
    *      to the tentative first player.                                    */
    fun verifySwap2(): Boolean {
        // TODO: implement
        return false
    }

    /* ****************************************************************************************************************
    The sequence of moves implied by the rule follows:
        - The first player puts one of the 26 openings.
        - The other player has the right to swap.
        - The white player puts the 4th move anywhere on board and declares whether there will be 1, 2, 3 or 4 fifth moves
          offered in the game.
        - The other player has a right to swap.
        -The black player puts as many 5th moves on the board as it was declared before. The fifth moves can not be symmetrical.
        - The white player chooses one 5th from these offerings and plays the 6th move.     */
    fun verifySoosorv(playerNum: Int, moveNum: Int, x: Int, y: Int): Boolean {
        // check https://en.wikipedia.org/wiki/Renju_opening_pattern
        val boardSize = 15 // only aplies for Renju
        return when (moveNum) {
            1 -> verifyCenterSquare(boardSize, x, y, 1) // is black and must be played in the center of the board.
            2 -> verifyCenterSquare(boardSize, x, y, 2) // is white and must be played in the center 3x3 square
            3 -> verifyCenterSquare(boardSize, x, y, 3) // is black and must be played in the center 5x5 square
            4 -> verifySoosorv4(playerNum)
            5 -> verifySoosorv5(playerNum)
            else -> { throw IllegalMove }
        }
    }

    /* ****************************** PRIVATE / AUXILIARY FUNCTIONS ************************************************ */
    private fun verifyCenterSquare(boardSize: Int, x: Int, y: Int, moveNum: Int): Boolean {
        val half = boardSize / 2
        if (moveNum == 1) return x == half && y == half
        val i = moveNum - 1
        return (x == half + i && y == half + i) || (x == half + i && y == half - i) || (x == half - i && y == half + i) || (x == half - i && y == half - i)
    }

    private fun verifySoosorv4(playerNum: Int): Boolean {
        // TODO: implement
        return false
    }

    private fun verifySoosorv5(playerNum: Int): Boolean {
        // TODO: implement
        return false
    }

    private fun verifyThreeIntersectionsAway(boardSize: Int, x: Int, y: Int): Boolean {
        val half = boardSize / 2
        return when (x) {
            half -> when (y) {
                half -> false
                half + 1 -> false
                half + 2 -> false
                half - 1 -> false
                half - 2 -> false
                else -> true
            }
            half + 1 -> when (y) {
                half -> false
                half + 1 -> false
                half + 2 -> false
                half - 1 -> false
                half - 2 -> false
                else -> true
            }
            half - 1 -> when (y) {
                half -> false
                half + 1 -> false
                half + 2 -> false
                half - 1 -> false
                half - 2 -> false
                else -> true
            }
            half + 2 -> when (y) {
                half -> false
                half + 1 -> false
                half + 2 -> false
                half - 1 -> false
                half - 2 -> false
                else -> true
            }
            half - 2 -> when (y) {
                half -> false
                half + 1 -> false
                half + 2 -> false
                half - 1 -> false
                half - 2 -> false
                else -> true
            }
            else -> true
        }
    }

    private fun verifyFourIntersectionsAway(boardSize: Int, x: Int, y: Int): Boolean {
        return if (!verifyThreeIntersectionsAway(boardSize, x, y)) {
            false
        } else {
            val half = boardSize / 2
            when (x) {
                half -> when (y) {
                    half + 3 -> false
                    half - 3 -> false
                    else -> true
                }
                half + 1 -> when (y) {
                    half + 3 -> false
                    half - 3 -> false
                    else -> true
                }
                half + 2 -> when (y) {
                    half + 3 -> false
                    half - 3 -> false
                    else -> true
                }
                half + 3 -> when (y) {
                    half -> false
                    half + 1 -> false
                    half + 2 -> false
                    half + 3 -> false
                    half - 1 -> false
                    half - 2 -> false
                    half - 3 -> false
                    else -> true
                }
                half - 1 -> when (y) {
                    half + 3 -> false
                    half - 3 -> false
                    else -> true
                }
                half - 2 -> when (y) {
                    half + 3 -> false
                    half - 3 -> false
                    else -> true
                }
                half - 3 -> when (y) {
                    half -> false
                    half + 1 -> false
                    half + 2 -> false
                    half + 3 -> false
                    half - 1 -> false
                    half - 2 -> false
                    half - 3 -> false
                    else -> true
                }
                else -> true
            }
        }
    }
}
