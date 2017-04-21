package com.chessmates.utility

/**
 * Represents a Chess game color.
 */
enum GameColor {

    WHITE {
        @Override
        GameColor opposite() { BLACK }

        @Override
        String toString() { 'white' }
    },
    BLACK {
        @Override
        GameColor opposite() { WHITE }

        @Override
        String toString() { 'black' }
    }

    abstract GameColor opposite()
    abstract String toString()

    static Optional<GameColor> fromString(String string) {
        if (!string) return Optional.empty()

        final lower = string.toLowerCase()

        switch (lower) {
            case 'black': return Optional.of(BLACK)
            case 'white': return Optional.of(WHITE)
            default: return Optional.empty()
        }
    }

}
