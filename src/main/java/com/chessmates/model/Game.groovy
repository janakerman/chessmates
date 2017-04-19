package com.chessmates.model

/**
 * A Lichess game.
 */
class Game {
    // TODO: Store more fields?
    final String id
    final Map<GameColor, String> players

    Game(String id, Map<GameColor, String> players) {
        this.id = id
        this.players = players
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Game game = (Game) o

        if (id != game.id) return false

        return true
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

    @Override
    String toString() { "<Game: ${id}>" }
}
