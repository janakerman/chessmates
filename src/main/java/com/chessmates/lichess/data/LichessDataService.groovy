package com.chessmates.lichess.data

import com.chessmates.model.Game
import com.chessmates.model.Player
import org.apache.commons.lang3.tuple.ImmutablePair

/**
 * Service for fetching data from the Lichess API.
 */
interface LichessDataService {

    /** Get all players. */
    List<Player> getPlayers()

    /** Get all games. */
    List<Game> getGames(List<Player> players)


}