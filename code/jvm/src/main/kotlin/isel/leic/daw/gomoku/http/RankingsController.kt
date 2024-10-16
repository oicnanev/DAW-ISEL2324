package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.domain.entities.Rank
import isel.leic.daw.gomoku.http.model.RankOutputModel
import isel.leic.daw.gomoku.http.model.StatsOutputModel
import isel.leic.daw.gomoku.services.StatsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RankingsController(
    private val statsService: StatsService
) {
    @GetMapping(Uris.Stats.STATS)
    fun getStats(): StatsOutputModel {
        return when (val res = statsService.getRankings()) {
            null -> StatsOutputModel(mutableListOf())
            else -> getStatsOutputModel(res)
        }
    }

    private fun getStatsOutputModel(res: MutableList<Rank>): StatsOutputModel {
        val rankings = mutableListOf<RankOutputModel>()
        for (r in res) {
            val rank = RankOutputModel(
                r.username,
                r.rank,
                r.variant,
                r.opening,
                r.gamesPlayed,
                r.wins,
                r.draws,
                Uris.Users.byId(r.id).toASCIIString()
            )
            rankings.add(rank)
        }
        return StatsOutputModel(rankings)
    }
}
