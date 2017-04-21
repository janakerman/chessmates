package com.chessmates.league

import spock.lang.Specification


class LeagueTableTest extends Specification {

    def league

    def "a league with no games should have no rows"() {
        given:
        league = new LeagueTable(0, 0)

        expect:
        league.getRows().size() == 0
    }

    def "a league with games should display the correct score"() {
        given:
        def games = [
                [id: '1', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'B' ]]],
                [id: '1', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'C' ]]],
                [id: '1', winner: 'black', players: [white: [ userId: 'B' ], black: [ userId: 'A' ]]],
                [id: '1', winner: 'white', players: [white: [ userId: 'C' ], black: [ userId: 'B' ]]],
                [id: '1', winner: 'black', players: [white: [ userId: 'B' ], black: [ userId: 'C' ]]],
        ]

        and:
        league = new LeagueTable(2, 1)

        when:
        league.add(games)

        then:
        league.getRows().size() == 3

        and: "playerRows are ordered with highest first"
        league.getRows().get(0).userId == 'A'
        league.getRows().get(1).userId == 'C'
        league.getRows().get(2).userId == 'B'

        and: "row contents are correct"
        league.getRows().get(0).wins == 3
        league.getRows().get(0).loses == 0
        league.getRows().get(0).played == 3
        league.getRows().get(0).points == 6

        league.getRows().get(1).wins == 2
        league.getRows().get(1).loses == 1
        league.getRows().get(1).played == 3
        league.getRows().get(1).points == 5

        league.getRows().get(2).wins == 0
        league.getRows().get(2).loses == 4
        league.getRows().get(2).played == 4
        league.getRows().get(2).points == 4
    }

}
