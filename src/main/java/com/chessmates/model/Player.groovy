package com.chessmates.model

/**
 * A Lichess player
 */
class Player {
    // TODO: Store more fields?
    /**
     * Lichess ID for the player entity.
     */
    final String id

    /**
     * Lichess username. The Lichess API provides both an ID and a username for a given player. At time of writing, these
     * are always equal.
     */
    final String username

    Player(String id, String username) {
        this.id = id
        this.username = username
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Player player = (Player) o

        if (id != player.id) return false

        return true
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

    @Override
    String toString() { "<Player: ${id}>" }
}
