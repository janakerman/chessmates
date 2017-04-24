package com.chessmates.lichess.data
/**
 * Service for fetching data from the Lichess API.
 */
interface LichessDataService {

    /** Fetch all players from Lichess and persist new items. */
    List updatePlayers()

    /** Fetch games for given players from the Lichess and persist
     * new items. */
    List updateGames(List players)


}