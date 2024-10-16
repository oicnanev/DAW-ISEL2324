package isel.leic.daw.gomoku.services

import isel.leic.daw.gomoku.domain.AuthenticatedUser
import isel.leic.daw.gomoku.domain.GameDomain
import isel.leic.daw.gomoku.domain.entities.Board
import isel.leic.daw.gomoku.domain.entities.BoardSize
import isel.leic.daw.gomoku.domain.entities.BoardUserData
import isel.leic.daw.gomoku.domain.entities.Game
import isel.leic.daw.gomoku.domain.entities.GameOpening
import isel.leic.daw.gomoku.domain.entities.GameVariant
import isel.leic.daw.gomoku.http.model.OutputGameRepresentation
import isel.leic.daw.gomoku.http.model.OutputLobbyGame
import isel.leic.daw.gomoku.http.model.OutputLobbyGamesListRepresentation
import isel.leic.daw.gomoku.repository.Transaction
import isel.leic.daw.gomoku.repository.TransactionManager
import isel.leic.daw.gomoku.utils.Either
import isel.leic.daw.gomoku.utils.failure
import isel.leic.daw.gomoku.utils.success
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

sealed class GameCreationError {
    object InvalidRequestContent : GameCreationError()
    object InvalidGame : GameCreationError()
    object InvalidUser : GameCreationError()
}

sealed class LobbyError {
    object GameAlreadyEnqueued : LobbyError()
    object InvalidBoardSize : LobbyError()
    object InvalidOpening : LobbyError()
    object InvalidVariant : LobbyError()
    object InvalidRequestContent : LobbyError()
    object InvalidUser : LobbyError()
    object WaitForPartner : LobbyError()
    object InvalidGame : LobbyError()
}

sealed class GameListingError {
    object InvalidToken : GameListingError()
}

sealed class GamePlayError {
    object InvalidToken : GamePlayError()
    object InvalidGame : GamePlayError()
    object InvalidMove : GamePlayError()
    object InvalidPlayer : GamePlayError()
    object InvalidPosition : GamePlayError()
    object InvalidRequestContent : GamePlayError()
}

typealias GameCreationResult = Either<GameCreationError, Int>
typealias LobbyGameResult = Either<LobbyError, Int>
typealias GameListingResult = Either<GameListingError, OutputLobbyGamesListRepresentation>
typealias GamePlayResult = Either<GamePlayError, OutputGameRepresentation>

@Component
class GameService(
    private val transactionManager: TransactionManager,
    private val gameDomain: GameDomain,
    private val clock: Clock
) {
    fun listLobbyGames(user: AuthenticatedUser): GameListingResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository

            run {
                val list = gameRepository.listLobbyGames()
                val res = OutputLobbyGamesListRepresentation(mutableListOf())
                if (list != null) {
                    for (lobbyGame in list) {
                        val game = OutputLobbyGame(
                            lobbyGame.id,
                            lobbyGame.variant,
                            lobbyGame.opening,
                            lobbyGame.boardSize,
                            lobbyGame.username,
                            lobbyGame.rank
                        )
                        res.add(game)
                    }
                }
                success(res)
            }
        }
    }

    fun addGameToLobby(user: AuthenticatedUser, variant: String, opening: String, boardSize: Int): LobbyGameResult {
        val gVariant = // variant validation
            gameDomain.tryGameVariant(variant) ?: return failure(LobbyError.InvalidVariant)
        val gOpening = // opening validation
            gameDomain.tryGameOpening(opening) ?: return failure(LobbyError.InvalidOpening)
        val gSize = // boardSize validation
            gameDomain.tryBoardSize(boardSize) ?: return failure(LobbyError.InvalidBoardSize)

        return transactionManager.run {
            val gameRepository = it.gameRepository

            if (gameRepository.hasUserAnActiveGameInLobby(user.user.id)) {
                failure(LobbyError.GameAlreadyEnqueued)
            } else {
                val gameTypeID = // get gameTypeID
                    gameRepository.getGameTypeIdByVariantOpeningAndBoardSize(gVariant, gOpening, gSize)
                val now = clock.now()
                val lobbyGameId = gameRepository.addGameToLobby(user.user.id, gameTypeID, now)

                success(lobbyGameId)
            }
        }
    }

    fun joinGame(user: AuthenticatedUser, lobbyGameId: Int): GameCreationResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository
            val userRepository = it.userRepository

            if (!gameRepository.hasUserAnActiveGameInLobby(user.user.id)) {
                failure(GameCreationError.InvalidUser)
            }
            if (!gameRepository.isLobbyGameActive(lobbyGameId)) {
                failure(GameCreationError.InvalidGame)
            } else {
                val lobbyGame = gameRepository.getLobbyGameById(lobbyGameId)
                if (lobbyGame == null) {
                    failure(GameCreationError.InvalidGame)
                }
                val typeId = gameRepository.getGameTypeIdByVariantOpeningAndBoardSize(
                    GameVariant(lobbyGame!!.variant),
                    GameOpening(lobbyGame.opening),
                    BoardSize(lobbyGame.boardSize)
                )
                val player1Id = userRepository.getUserByUsername(lobbyGame.username)!!.id
                val player2Id = user.user.id
                val firstPlayer = gameDomain.getFirstPlayer() // randomize first player
                val boardUserData1: BoardUserData
                val boardUserData2: BoardUserData
                if (firstPlayer == 1) {
                    boardUserData1 = BoardUserData(player1Id, "b", null)
                    boardUserData2 = BoardUserData(player2Id, "w", null)
                } else {
                    boardUserData1 = BoardUserData(player1Id, "w", null)
                    boardUserData2 = BoardUserData(player2Id, "b", null)
                }
                val board = Board(lobbyGame.boardSize, boardUserData1, boardUserData2)
                val now = Clock.System.now()
                val game = Game(null, typeId, board, player1Id, player2Id, firstPlayer, "in-progress", now, now)
                val gameId = gameRepository.createGame(game)
                gameRepository.deactivateLobbyGame(lobbyGameId)
                success(gameId)
            }
        }
    }

    fun checkGameAlreadyHaveAPartner(user: AuthenticatedUser): LobbyGameResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository

            // check if user has an active game in lobby
            if (gameRepository.hasUserAnActiveGameInLobby(user.user.id)) {
                failure(LobbyError.InvalidUser)
            } else {
                // check if user has an active game in progress
                val matchGameId = gameRepository.getInProgressGameIdByPlayer1Id(user.user.id)
                if (matchGameId != null) {
                    success(matchGameId)
                } else {
                    failure(LobbyError.WaitForPartner)
                }
            }
        }
    }

    fun getGameById(user: AuthenticatedUser, gameId: Int): GamePlayResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository
            val userRepository = it.userRepository
            val game = gameRepository.getGameById(gameId)
            val opening: String
            val variant: String
            val user1: String?
            val user2: String?
            if (game != null) {
                opening = gameRepository.getOpeningByGameTypeId(game.gameTypeId)
                variant = gameRepository.getVariantByGameTypeId(game.gameTypeId)
                user1 = userRepository.getUserUsernameById(game.player1)
                user2 = userRepository.getUserUsernameById(game.player2)
                success(
                    OutputGameRepresentation(
                        gameId,
                        game.board,
                        opening,
                        variant,
                        game.currentPlayer,
                        user1!!,
                        user2!!,
                        game.state,
                        game.startTime.epochSeconds,
                        game.updated.epochSeconds
                    )
                )
            } else {
                failure(GamePlayError.InvalidGame)
            }
        }
    }

    fun playMove(gameId: Int, user: AuthenticatedUser, x: Int, y: Int): GamePlayResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository
            val userRepository = it.userRepository
            val game = gameRepository.getGameById(gameId)
            val opening: String
            val variant: String
            val user1: String?
            val user2: String?
            if (game != null) {
                opening = gameRepository.getOpeningByGameTypeId(game.gameTypeId)
                variant = gameRepository.getVariantByGameTypeId(game.gameTypeId)
                user1 = userRepository.getUserUsernameById(game.player1)
                user2 = userRepository.getUserUsernameById(game.player2)

                // check if is the correct player turn
                if (!gameDomain.checkTurn(game.currentPlayer, user.user.id, game.player1)) {
                    failure(GamePlayError.InvalidPlayer)
                } else if (!gameDomain.checkPosition(variant, opening, game.board, x, y, game.currentPlayer)) {
                    // check if desired position is valid
                    failure(GamePlayError.InvalidPosition)
                } else {
                    // add turn to board
                    val now = Clock.System.now()
                    val boardAfterTurn =
                        gameDomain.updateBoard(game.board, x, y, game.currentPlayer, now)

                    // update board in DB
                    val newCurrentPlayer = game.currentPlayer % 2 + 1
                    gameRepository.updateGameBoard(game.copy(board = boardAfterTurn), gameId, newCurrentPlayer, now.epochSeconds)
                    // check victory
                    if (gameDomain.checkVictory(variant, boardAfterTurn, game.currentPlayer)) {
                        success(updateVictory(it, gameId, game, user1, user2, opening, variant, now))
                    }
                    // check draw
                    if (gameDomain.checkDraw(boardAfterTurn)) {
                        success(updateDraw(it, game, gameId, user1, user2, opening, variant, now))
                    }

                    val updatedGame = gameRepository.getGameById(gameId)
                    success(
                        OutputGameRepresentation(
                            gameId,
                            updatedGame!!.board,
                            opening,
                            variant,
                            updatedGame.currentPlayer,
                            user1!!,
                            user2!!,
                            updatedGame.state,
                            updatedGame.startTime.epochSeconds,
                            updatedGame.updated.epochSeconds
                        )
                    )
                }
            } else {
                failure(GamePlayError.InvalidGame)
            }
        }
    }

    fun giveUpGameFromLobby(gameId: Int, user: AuthenticatedUser): LobbyGameResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository
            val userRepository = it.userRepository
            val game = gameRepository.getLobbyGameById(gameId)
            if (game != null) {
                val user1 = userRepository.getUserByUsername(game.username)
                if (user1!!.id == user.user.id) {
                    gameRepository.deactivateLobbyGame(gameId)
                    success(gameId)
                } else {
                    failure(LobbyError.InvalidUser)
                }
            } else {
                failure(LobbyError.InvalidGame)
            }
        }
    }

    private fun updateVictory(
        transaction: Transaction,
        gameId: Int,
        game: Game,
        user1: String?,
        user2: String?,
        opening: String,
        variant: String,
        now: Instant
    ): GamePlayResult {
        val gameRepository = transaction.gameRepository
        val userRepository = transaction.userRepository
        val gameUser1 = userRepository.getUserById(game.player1)
        val gameUser2 = userRepository.getUserById(game.player2)
        if (game.currentPlayer == 1) {
            gameRepository.updateGameState(game.copy(state = "player1_won"), gameId, now.epochSeconds)
            userRepository.updateUserStatistics(
                game.player1,
                gameUser1!!.rank + 3,
                gameUser1.gamesPlayed + 1,
                gameUser1.wins + 1,
                gameUser1.draws
            )
            userRepository.updateUserStatistics(
                game.player2,
                gameUser2!!.rank,
                gameUser2.gamesPlayed + 1,
                gameUser2.wins,
                gameUser2.draws
            )
        } else {
            gameRepository.updateGameState(game.copy(state = "player2_won"), gameId, now.epochSeconds)
            userRepository.updateUserStatistics(
                game.player1,
                gameUser1!!.rank,
                gameUser1.gamesPlayed + 1,
                gameUser1.wins,
                gameUser1.draws
            )
            userRepository.updateUserStatistics(
                game.player2,
                gameUser2!!.rank + 3,
                gameUser2.gamesPlayed + 1,
                gameUser2.wins + 1,
                gameUser2.draws
            )
        }
        val updatedGame = gameRepository.getGameById(gameId)
        return success(
            OutputGameRepresentation(
                gameId,
                updatedGame!!.board,
                opening,
                variant,
                updatedGame.currentPlayer,
                user1!!,
                user2!!,
                updatedGame.state,
                updatedGame.startTime.epochSeconds,
                updatedGame.updated.epochSeconds
            )
        )
    }

    private fun updateDraw(
        transaction: Transaction,
        game: Game,
        gameId: Int,
        user1: String?,
        user2: String?,
        opening: String,
        variant: String,
        now: Instant
    ): GamePlayResult {
        val gameRepository = transaction.gameRepository
        val userRepository = transaction.userRepository
        val gameUser1 = userRepository.getUserById(game.player1)
        val gameUser2 = userRepository.getUserById(game.player2)
        gameRepository.updateGameState(game.copy(state = "draw"), gameId, now.epochSeconds)
        userRepository.updateUserStatistics(
            game.player1,
            gameUser1!!.rank + 1,
            gameUser1.gamesPlayed + 1,
            gameUser1.wins,
            gameUser1.draws + 1
        )
        userRepository.updateUserStatistics(
            game.player2,
            gameUser2!!.rank + 1,
            gameUser2.gamesPlayed + 1,
            gameUser2.wins,
            gameUser2.draws + 1
        )
        val updatedGame = gameRepository.getGameById(gameId)
        return success(
            OutputGameRepresentation(
                gameId,
                updatedGame!!.board,
                opening,
                variant,
                updatedGame.currentPlayer,
                user1!!,
                user2!!,
                updatedGame.state,
                updatedGame.startTime.epochSeconds,
                updatedGame.updated.epochSeconds
            )
        )
    }

    fun giveUpGame(authenticatedUser: AuthenticatedUser): GamePlayResult {
        return transactionManager.run {
            val gameRepository = it.gameRepository
            val userRepository = it.userRepository

            // get the game user is currently playing
            val game = gameRepository.getInProgressGameByPlayerId(authenticatedUser.user.id)

            // give victory to adversary
            if (game != null) {
                val gameUser1 = userRepository.getUserById(game.player1)
                val gameUser2 = userRepository.getUserById(game.player2)
                val now = Clock.System.now()

                if (game.player1 == authenticatedUser.user.id) {
                    gameRepository.updateGameState(game.copy(state = "player2_won"), game.id!!, now.epochSeconds)
                    userRepository.updateUserStatistics(
                        game.player1,
                        gameUser1!!.rank,
                        gameUser1.gamesPlayed + 1,
                        gameUser1.wins,
                        gameUser1.draws
                    )
                    userRepository.updateUserStatistics(
                        game.player2,
                        gameUser2!!.rank + 3,
                        gameUser2.gamesPlayed + 1,
                        gameUser2.wins + 1,
                        gameUser2.draws
                    )
                } else {
                    gameRepository.updateGameState(game.copy(state = "player1_won"), game.id!!, now.epochSeconds)
                    userRepository.updateUserStatistics(
                        game.player1,
                        gameUser1!!.rank + 3,
                        gameUser1.gamesPlayed + 1,
                        gameUser1.wins + 1,
                        gameUser1.draws
                    )
                    userRepository.updateUserStatistics(
                        game.player2,
                        gameUser2!!.rank,
                        gameUser2.gamesPlayed + 1,
                        gameUser2.wins,
                        gameUser2.draws
                    )
                }
                val updatedGame = gameRepository.getGameById(game.id)
                if (updatedGame != null) {
                    val opening = gameRepository.getOpeningByGameTypeId(game.gameTypeId)
                    val variant = gameRepository.getVariantByGameTypeId(game.gameTypeId)
                    val user1 = userRepository.getUserUsernameById(game.player1)
                    val user2 = userRepository.getUserUsernameById(game.player2)
                    success(
                        OutputGameRepresentation(
                            updatedGame.id!!,
                            updatedGame.board,
                            opening,
                            variant,
                            updatedGame.currentPlayer,
                            user1!!,
                            user2!!,
                            updatedGame.state,
                            updatedGame.startTime.epochSeconds,
                            updatedGame.updated.epochSeconds
                        )
                    )
                } else {
                    failure(GamePlayError.InvalidGame)
                }
            } else {
                failure(GamePlayError.InvalidGame)
            }
        }
    }
}
