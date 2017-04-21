package com.chessmates.league

import com.chessmates.repository.GameRepository
import spock.lang.Specification
import spock.lang.Subject

class SimpleLeagueTableServiceTest extends Specification {

    @Subject
    SimpleLeagueTableService simpleLeagueTableService

    GameRepository gameRepository

    def setup() {
        gameRepository = Mock(GameRepository)
    }

    def "uses all games to create league table"() {
        given:
        simpleLeagueTableService = new SimpleLeagueTableService(gameRepository)

        when:
        simpleLeagueTableService.getTable()

        then:
        1 * gameRepository.findAll()
    }

}
