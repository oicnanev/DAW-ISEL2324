package isel.leic.daw.gomoku.http

import isel.leic.daw.gomoku.domain.entities.About
import isel.leic.daw.gomoku.services.AboutService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AboutController(
    private val aboutService: AboutService
) {
    @GetMapping(Uris.About.ABOUT)
    fun getInfo(): About = aboutService.getAbout()
}
