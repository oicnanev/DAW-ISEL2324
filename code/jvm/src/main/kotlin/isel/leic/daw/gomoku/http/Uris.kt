package isel.leic.daw.gomoku.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    const val PREFIX = "/api"
    const val HOME = PREFIX

    fun home(): URI = URI(HOME)

    object Users {
        const val CREATE = "$PREFIX/users"
        const val TOKEN = "$PREFIX/users/token"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_BY_ID = "$PREFIX/users/{id}"
        const val HOME = "$PREFIX/me"

        fun byId(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun home(): URI = URI(HOME)
        fun login(): URI = URI(TOKEN)
        fun register(): URI = URI(CREATE)
    }

    object Lobby {
        const val LOBBY = "$PREFIX/lobby"
        const val JOINGAME = "$PREFIX/joingame"
        const val CHECKMATCH = "$PREFIX/checkmatch"
        const val LOBBYID = "$PREFIX/lobby/{id}"
        const val JOINGAMEID = "$PREFIX/joingame/{lobbyGameId}"

        fun byId(id: Int) = UriTemplate(LOBBYID).expand(id)
    }

    object Game {
        const val GAME = "$PREFIX/game"
        const val GIVEUP = "$PREFIX/game/giveup"
        const val GAMEID = "$PREFIX/game/{id}"

        fun byId(id: Int) = UriTemplate(GAMEID).expand(id)
    }

    object Stats {
        const val STATS = "$PREFIX/stats"
        fun stats(): URI = URI(STATS)
    }

    object About {
        const val ABOUT = "$PREFIX/about"
        fun about(): URI = URI(ABOUT)
    }

    object Rules {
        const val RULES = "$PREFIX/rules"
        fun rules(): URI = URI(RULES)
    }

    object SystemInfo {
        const val INFO = "$PREFIX/info"
        fun info(): URI = URI(INFO)
    }
}
