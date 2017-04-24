package com.chessmates.league

import spock.lang.Specification

import java.util.stream.Collectors


class LeagueTableTest extends Specification {

    def league

    def "a league with no games should have no rows"() {
        given:
        league = new LeagueTable(0, 0)

        expect:
        league.getRows().size() == 0
    }


    def "ignores games without result"() {
        given: "game with no winner and no status indicating draw"
        def games = [
                [id: '1', players: [white: [ userId: 'A' ], black: [ userId: 'B' ]]],
        ]

        and:
        league = new LeagueTable(2f, 1f, 0.5f)

        when:
        league.add games

        then:
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
        league = new LeagueTable(2f, 1f, 0.5f)

        when:
        league.add(games)

        then:
        league.getRows().size() == 3

        and: "playerRows are ordered with highest first"
        assertPlayerOrder(league.getRows(), ['A', 'C', 'B'])

        and: "row contents are correct"
        assertPlayerRow(league.getRows(), 1, 'A', 3, 0, 0, 3, 6)
        assertPlayerRow(league.getRows(), 2, 'C', 2, 0, 1, 3, 4.5)
        assertPlayerRow(league.getRows(), 3, 'B', 0, 0, 4, 4, 2)
    }

    def "handles draws"() {
        given:
        def games = [
                [id: '1', status: 'outoftime', players: [white: [ userId: 'B' ], black: [ userId: 'A' ]]],
                [id: '1', status: 'outoftime', players: [white: [ userId: 'B' ], black: [ userId: 'A' ]]],
                [id: '1', status: 'draw', players: [white: [ userId: 'C' ], black: [ userId: 'B' ]]],
                [id: '1', status: 'draw', players: [white: [ userId: 'C' ], black: [ userId: 'A' ]]],
        ]

        and: "winning game with status"
        games.add([id: '1', status: 'outoftime', winner: 'black', players: [white: [ userId: 'C' ], black: [ userId: 'A' ]]])

        and:
        league = new LeagueTable(2f, 1f, 0.5f)

        when:
        league.add(games)

        then:
        league.getRows().size() == 3

        and:
        assertPlayerOrder(league.getRows(), ['A', 'B', 'C'])

        and: "row contents are correct"
        assertPlayerRow(league.getRows(), 1, 'A', 1, 3, 0, 4, 5)
        assertPlayerRow(league.getRows(), 2, 'B', 0, 3, 0, 3, 3)
        assertPlayerRow(league.getRows(), 3, 'C', 0, 2, 1, 3, 2.5)
    }

    def "equal rows are sorted alphabetically"() {
        given:
        def games = [
                [id: '1', status: 'outoftime', players: [white: [ userId: 'C' ], black: [ userId: 'A' ]]],
                [id: '1', status: 'outoftime', players: [white: [ userId: 'B' ], black: [ userId: 'A' ]]],
        ]

        and:
        league = new LeagueTable(2f, 1f, 0.5f)

        when:
        league.add games

        then:
        assertPlayerOrder(league.getRows(), ['A', 'B', 'C'])
    }

    def "shared position for rows that have same points"() {
        given:
        def games = [
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'B' ]]],
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'C' ]]],
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'D' ]]],
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'A' ], black: [ userId: 'E' ]]],
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'B' ], black: [ userId: 'D' ]]],
                [id: '1', status: 'outoftime', winner: 'white', players: [white: [ userId: 'C' ], black: [ userId: 'E' ]]],
        ]

        and:
        league = new LeagueTable(2f, 1f, 0.5f)

        when:
        league.add games

        then:
        assertPosition(league.getRows(), 'A', 1)
        assertPosition(league.getRows(), 'B', 2)
        assertPosition(league.getRows(), 'C', 2)
        assertPosition(league.getRows(), 'D', 3)
        assertPosition(league.getRows(), 'E', 3)
    }

    private static tableRow(rows, player) {
        rows.find { it.userId == player }
    }

    private static void assertPosition(rows, player, position) {
        def row = tableRow(rows, player)
        assert row.position == position
    }

    private static void assertPlayerRow(rows, position, player, wins, draws, loses, played, points) {
        def row = tableRow(rows, player)
        assert row.position == position
        assert row.wins == wins
        assert row.draws == draws
        assert row.loses == loses
        assert row.played == played
        assert row.points == points
    }

    private static void assertPlayerOrder(rows, players) {
        assert rows.stream().map({ it.userId }).collect(Collectors.toList()) == players
    }
}
