package com.chessmates.controller

import com.chessmates.service.EntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for returning information about Lichess team Players.
 */
@RestController
@CrossOrigin
class PersonController {

    @Autowired
    EntityService entityService

    /**
     * Return a list of all team players.
     */
    @GetMapping(value = 'players')
    List getPlayers() {
        entityService.getPlayers()
    }

    /**
     * Return a player of a given ID.
     */
    @GetMapping(value = 'player/{id}')
    getPlayer(@PathVariable('id') String id) {
        entityService.getPlayer(id)
    }

}
