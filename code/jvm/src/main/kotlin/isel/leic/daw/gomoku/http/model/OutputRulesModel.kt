package isel.leic.daw.gomoku.http.model

class OutputRulesModel {
    val generic = "Players alternate turns placing a stone of their color on an empty intersection. Black plays first. The winner is the first player to form an unbroken line of five stones of their color horizontally, vertically, or diagonally. In some rules, this line must be exactly five stones long; six or more stones in a row does not count as a win and is called an overline. If the board is completely filled and no one can make a line of 5 stones, then the game ends in a draw."
    val specific = RulesSpecificModel()
    val variants = VariantsModel()
    val openings = OpeningsModel()
}

class RulesSpecificModel {
    val swapAfterFirstMove = RuleModel(
        name = "Swap after 1st move",
        description = "Once the first player places a stone, the second player may choose to swap colors and play as black."
    )
    val threeAndThree = RuleModel(
        name = "Three and three",
        description = "A player may not place a stone that simultaneously creates two open lines of three stones for the opponent."
    )
    val fourAndFour = RuleModel(
        name = "Four and four",
        description = "A player may not place a stone that simultaneously creates two open lines of four stones for the opponent."
    )
    val overline = RuleModel(
        name = "Overline",
        description = "A player may not place a stone that simultaneously creates an open line of six or more stones for the opponent."
    )
    val capture = RuleModel(
        name = "Capture",
        description = "A player may capture an opponent's stone or stones by completely surrounding them with their own stones. Captured stones are removed from the board and kept by the capturing player as prisoners."
    )
}

class VariantsModel {
    val freestyle = RuleModel(
        name = "Freestyle",
        description = "Players have no restrictions on wining by creating a line of five or more stones, with each player alternating turns placing one stone at a time."
    )
    val freestyleSwap = RuleModel(
        name = "Freestyle Swap",
        description = "Uses the swap after 1st move rule. The rest of the game is played under the freestyle rules."
    )
    val renju = RuleModel(
        name = "Renju",
        description = "Is played on a 15x15 board, with the rule of three and three, four and four, and overline apllied to Black only."
    )
    val caro = RuleModel(
        name = "Caro",
        description = "The winner must have an overline or an unbroken row of five stones that is not blocked at either end (overlines are imune to this rule)."
    )
    val omok = RuleModel(
        name = "Omok",
        description = "Is played on a 19x19 board, with the rule of three and three."
    )
    val ninukiRenju = RuleModel(
        name = "Ninuki-Renju",
        description = "The winner is the player either to make a perfect five row, or to capture five pairs of the opponet's stones. Is played on a 15x15 board, with the rule of three and three and overline. It also allows the game to continue after a player has formed a row of five stones if their opponent can capture a pair accross the line"
    )
    val pente = RuleModel(
        name = "Pente",
        description = "Has the same custodial capture method of Ninuki Renju, but is played on a 19x19 board, and does not use the rules of three and three, four and for, or overline."
    )
}

class OpeningsModel {
    val open = RuleModel(
        name = "Open",
        description = "No tournment opennings are applied."
    )
    val swap = RuleModel(
        name = "Swap",
        description = "The swap after 1st move rule is applied."
    )
    val swap2 = RuleModel(
        name = "Swap 2",
        description = "The tentative first player places three stones on the board, two black and one white. The tentative second player then has three option: 1. He can choose to play as white and place a second white stone; 2. He can swap their color and choose to play as black; 3. Or he can place two more stones, one black and one white, and let the tentative first player choose to play as black or white."
    )
    val pro = RuleModel(
        name = "Pro",
        description = "The first player's first stone must be placed in the center of the board. The second player's first stone may be placed anywhere on the board. The first player's second stone must be placed at least three intersections away from the first stone (two empty intersections in between the two stones)."
    )
    val longPro = RuleModel(
        name = "Long Pro",
        description = "The first player's first stone must be placed in the center of the board. The second player's first stone may be placed anywhere on the board. The first player's second stone must be placed at least four intersections away from the first stone (three empty intersections in between the two stones)."
    )
    val soosorv = SoosorvModel()
}

class SoosorvModel {
    val sequenceOfMoves = SequenceOfMovesModel()
    val restrictionsOnFirstMove = RestrictionsOnFirstMoveModel()
}

class SequenceOfMovesModel {
    val firstMove = RuleModel(
        name = "First Move",
        description = "The first player puts one of the 26 allowed openings acordingly to the restrigions on first move."
    )
    val secondMove = RuleModel(
        name = "Second Move",
        description = "The other player has the right to swap."
    )
    val thirdMove = RuleModel(
        name = "Third Move",
        description = "The white player puts the 4th move anywhere on board and declares whether there will be 1, 2, 3 or 4 fifth moves offered in the game."
    )
    val fourthMove = RuleModel(
        name = "Fourth Move",
        description = "The other player has a right to swap."
    )
    val fifthMove = RuleModel(
        name = "Fifth Move",
        description = "The black player puts as many 5th moves on the board as it was declared before. The fifth moves can not be symmetrical."
    )
    val sixthMove = RuleModel(
        name = "Sixth Move",
        description = "The white player chooses one 5th from these offerings and plays the 6th move."
    )
}

class RestrictionsOnFirstMoveModel {
    val first = RuleModel(
        name = "1",
        description = "The first move (black) must be played in the center of the board."
    )
    val second = RuleModel(
        name = "2",
        description = "The second move (white) must be played in the center 3x3 square."
    )
    val third = RuleModel(
        name = "3",
        description = "The third move (black) must be played in the center 5x5 square."
    )
}

data class RuleModel(
    val name: String,
    val description: String
)
