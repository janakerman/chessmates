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

    private final Map<String, LeagueRow> playerRows = new HashMap()

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
    }

    /** Get an ordered set of league table playerRows representing the games in the league. */
    List<LeagueRow> getRows() {
        new ArrayList(playerRows.values())
            .sort { a, b -> b.points.compareTo(a.points) }
    }

    private LeagueRow getLeagueRow(rows, player) {
        rows.putIfAbsent(player, new LeagueRow(player))
        rows.get(player)
    }

    /** Represents a single players results in the league. */
    final class LeagueRow {

        final String userId

        private Integer wins = 0
        private Integer draws = 0
        private Integer loses = 0

        LeagueRow(String playerId) {
            this.userId = playerId
        }

        Integer getWins() { wins }
        Integer getDraws() { draws }
        Integer getLoses() { loses }
        Integer getPlayed() { wins + draws + loses }
        Float getPoints() { (wins * winPoints) + (draws * drawPoints) + (loses * lossPoints)}

        @Override
        String toString() { "${this.class.name}{winPoints=${winPoints}, wins=${wins}, draws=${draws}, loses=${loses}}" }

    }



}
