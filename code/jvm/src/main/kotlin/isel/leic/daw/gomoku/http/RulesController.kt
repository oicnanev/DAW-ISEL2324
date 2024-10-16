package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.http.model.OutputRulesModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RulesController() {
    @GetMapping(Uris.Rules.RULES, produces = ["application/json"])
    fun getRules() = OutputRulesModel()
}
