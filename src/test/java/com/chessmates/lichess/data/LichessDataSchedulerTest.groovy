package com.chessmates.lichess.data

import com.chessmates.service.EntityService
import spock.lang.Specification
import spock.lang.Subject

class LichessDataSchedulerTest extends Specification {

    @Subject
    LichessDataScheduler lichessDataScheduler

    LichessDataService lichessDataService
    EntityService entityService

    def setup() {
        lichessDataService = Mock(LichessDataService)
        entityService = Mock(EntityService)

        lichessDataScheduler = new LichessDataScheduler(lichessDataService, entityService)
    }

    def "Scheduler uses existing players to update games"() {
        given:
        final existingPlayers = [[id: 'a'], [id: 'b'], [id: 'c']]
        entityService.getPlayers() >> existingPlayers

        when:
        lichessDataScheduler.updateData()

        then:
        1 * lichessDataService.updateGames(existingPlayers)
    }

}
