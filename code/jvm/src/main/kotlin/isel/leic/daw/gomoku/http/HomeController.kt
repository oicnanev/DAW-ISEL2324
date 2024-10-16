package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.http.model.OutputHomeModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping(Uris.HOME)
    fun getHome() = OutputHomeModel()
}
