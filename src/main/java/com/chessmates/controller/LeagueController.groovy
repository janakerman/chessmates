package com.chessmates.controller

import com.chessmates.league.LeagueTableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for handling league requests
 */
@RestController
@CrossOrigin
class LeagueController {

    LeagueTableService leagueTableService

    @Autowired
    LeagueController(LeagueTableService leagueTableService) {
        this.leagueTableService = leagueTableService
    }

    /**
     * Return the team league.
     */
    @GetMapping(value = 'league/table')
    List getPlayers() { leagueTableService.getTable().getRows() }

}
