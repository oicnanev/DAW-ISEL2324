package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.http.model.OutputAPIDescription
import isel.leic.daw.gomoku.services.AboutService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InfoController(
    private val aboutService: AboutService
) {
    @GetMapping(Uris.SystemInfo.INFO, produces = ["application/vnd.siren+json"])
    fun getInfo() = OutputAPIDescription()
}
