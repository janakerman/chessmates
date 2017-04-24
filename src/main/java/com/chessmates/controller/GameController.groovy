package com.chessmates.controller

import com.chessmates.service.EntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for returning information about team Lichess games.
 */
@RestController
@CrossOrigin
class GameController {

    @Autowired
    EntityService entityService

    /**
     * Return a list of all unique games played between all team members.
     */
    @GetMapping(value = 'games')
    List getGames() { entityService.getGames() }

    /**
     * Return a game of a given ID.
     */
    @GetMapping(value = 'game/{id}')
    getGame(@PathVariable('id') String id) {
        entityService.getGame(id)
    }

}
