package com.chessmates.league

import com.chessmates.utility.GameColor
import org.slf4j.Logger

/**
 * A simple league table.
 */
class LeagueTable {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LeagueTable)

    /** Points gained for a win. */
    final winPoints
    /** Points gained for a loss. */
    final lossPoints

    private final Map<String, LeagueRow> playerRows = new HashMap()

    LeagueTable(Integer winPoints, Integer lossPoints) {
        this.winPoints = winPoints
        this.lossPoints = lossPoints
    }

    /** Add games to the league table. **/
    protected void add(Collection games) {
        games.each { game ->
            final maybeWinColor = GameColor.fromString(game.winner)

            if (!maybeWinColor.isPresent()) {
                logger.error "Couldn't add game: ${game.id} to league. Game has no winner field."
                return
            }

            final winColor = maybeWinColor.get()

            final String winner = game.players[winColor.toString()].userId
            final String looser = game.players[winColor.opposite().toString()].userId

            playerRows.putIfAbsent(winner, new LeagueRow(winner))
            playerRows.putIfAbsent(looser, new LeagueRow(looser))

            playerRows.get(winner).wins++
            playerRows.get(looser).loses++
        }
    }

    /** Get an ordered set of league table playerRows representing the games in the league. */
    List<LeagueRow> getRows() {
        new ArrayList(playerRows.values())
            .sort { a, b -> b.points.compareTo(a.points) }
    }

    /** Represents a single players results in the league. */
    final class LeagueRow {

        final String userId

        private Integer wins = 0
        private Integer loses = 0

        LeagueRow(String playerId) {
            this.userId = playerId
        }

        Integer getWins() { wins }
        Integer getLoses() { loses }
        Integer getPlayed() { wins + loses }
        Integer getPoints() { (wins * winPoints) + (loses * lossPoints)}
    }

}
