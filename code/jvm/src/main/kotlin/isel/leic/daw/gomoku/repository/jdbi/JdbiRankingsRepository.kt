package isel.leic.daw.gomoku.repository.jdbi

import isel.leic.daw.gomoku.domain.entities.Rank
import isel.leic.daw.gomoku.repository.UserRankingRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiRankingsRepository(
    private val handle: Handle
) : UserRankingRepository {
    override fun getRankings(): MutableList<Rank>? {
        return handle.createQuery(
            """
            select * from (
            select u.id, username, rank, variant, opening, view.games_played, view.wins, view.draws,
            RANK() over (order by u.games_played desc) as rank_sum
            from dbo.user_stats_by_game_type_view as view
            join dbo.users u on u.id = view.user_id
            join dbo.types t on t.id = view.type_id
            where view.games_played > 0 and rank > 0
            ) as sub
            where rank_sum > 0
            """.trimMargin()
        )
            .mapTo<Rank>()
            .list()
    }
}
