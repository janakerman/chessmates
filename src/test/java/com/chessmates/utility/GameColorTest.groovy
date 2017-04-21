package com.chessmates.utility

import spock.lang.Specification

class GameColorTest extends Specification {

    def "returns opposite color"() {
        expect:
        original.opposite() == opposite

        where:
        original        | opposite
        GameColor.BLACK | GameColor.WHITE
        GameColor.WHITE | GameColor.BLACK
    }

    def "parses valid strings"() {
        expect:
        GameColor.fromString(string).get() == expected

        where:
        string  | expected
        'BLACK' | GameColor.BLACK
        'black' | GameColor.BLACK
        'WHITE' | GameColor.WHITE
        'white' | GameColor.WHITE
    }

    def "empty optional for invalid strings"() {
        expect:
        GameColor.fromString(string) == expected

        where:
        string  | expected
        null    | Optional.empty()
        ''      | Optional.empty()
        'a'     | Optional.empty()
        'awhite'| Optional.empty()
        'whitea'| Optional.empty()
    }

}
