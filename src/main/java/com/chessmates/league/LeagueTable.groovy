package com.chessmates.league

import com.chessmates.utility.GameColor
import org.slf4j.Logger

/**
 * A simple league table.
 */
class LeagueTable {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LeagueTable)
    private static final DRAW_STRING = 'draw'
    private static final OUT_OF_TIME = 'outoftime'

    /** Points gained for a win. */
    final winPoints
    /** Points gained for a draw. */
    final drawPoints
    /** Points gained for a loss. */
    final lossPoints

    private final List<LeagueRow> playerRows = new LinkedList<>()

    LeagueTable(Float winPoints = 0, Float drawPoints = 0, Float lossPoints = 0) {
        this.winPoints = winPoints
        this.drawPoints = drawPoints
        this.lossPoints = lossPoints
    }

    /** Add games to the league table. **/
    protected void add(Collection games) {
        games.each { game ->
            final maybeWinColor = GameColor.fromString(game.winner)
            final isWinner = maybeWinColor.isPresent()
            final isDraw = game.status == DRAW_STRING || game.status == OUT_OF_TIME && !isWinner

            // If we don't have a winner and we don't have a draw then something is wrong here!
            if (!isWinner && !isDraw) {
                logger.error "Couldn't add game: ${game.id} to league. Game has no winner field."
                return
            }

            // Give both players points for a draw.
            if (isDraw) {
                game.players.values().each { playerEntry -> getLeagueRow(playerRows, playerEntry.userId).draws++ }
                return
            }

            // Give winner & looser points.
            final winColor = maybeWinColor.get()
            final String winner = game.players[winColor.toString()].userId
            final String looser = game.players[winColor.opposite().toString()].userId

            getLeagueRow(playerRows, winner).wins++
            getLeagueRow(playerRows, looser).loses++
        }

        updatePositions(playerRows)
    }

    List<LeagueRow> getRows() { playerRows }

    private LeagueRow getLeagueRow(List<LeagueRow> rows, player) {
        LeagueRow row = rows.find { it.userId == player}
        if (!row) {
            row = new LeagueRow(player)
            rows.add row
        }
        return row
    }

    private static updatePositions(rows) {
        def nextPosition = 1
        rows.sort { a, b ->
                final pointResult = b.points.compareTo(a.points)
                if (pointResult != 0) return pointResult
                else a.userId.compareTo(b.userId)
            }
            .eachWithIndex { row, i ->
                final previousRow = i > 0 ? rows.get(i-1) : null

                if (previousRow && row.points == previousRow.points) {
                    row.position = previousRow.position
                    return
                }

                row.position = nextPosition++
            }
    }

    /** Represents a single players results in the league. */
    final class LeagueRow {

        final String userId

        private Integer position = null
        private Integer wins = 0
        private Integer draws = 0
        private Integer loses = 0

        LeagueRow(String playerId) {
            this.userId = playerId
        }

        Integer getPosition() { position }
        Integer getWins() { wins }
        Integer getDraws() { draws }
        Integer getLoses() { loses }
        Integer getPlayed() { wins + draws + loses }
        Float getPoints() { (wins * winPoints) + (draws * drawPoints) + (loses * lossPoints)}

        @Override
        String toString() { "${this.class.name}{userId=${userId}, position=${position}, winPoints=${winPoints}, wins=${wins}, draws=${draws}, loses=${loses}}" }

    }



}
