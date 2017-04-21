package com.chessmates.league

/**
 * A service providing league tables.
 */
interface LeagueTableService {

    /** Get a league table between all games played by team players. */
    LeagueTable getTable()

}