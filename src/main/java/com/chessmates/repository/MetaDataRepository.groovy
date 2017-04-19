package com.chessmates.repository

import com.chessmates.model.Game
import com.chessmates.model.Player
import com.google.common.collect.ImmutableMap
import org.apache.commons.lang3.tuple.ImmutablePair

/**
 * A service for storing & accessing information about the current state of Lichess data.
 */
interface MetaDataRepository {

    /** Save latest player fetched from the Lichess API. */
    void saveLatestPlayer(Player player)

    /** Get latest player fetched from the Lichess API. */
    Player getLatestPlayer()

    /** Save the latest game requested from the Lichess API for a given set of opponents. */
    void saveLatestGame(Player player, Player opponent, Game game)

    /** Get the latest game request from the Lichess API for a given set of opponents.
     * ImmutableMap is returned to indicate that changes to this store don't affect the store contents.
     */
    ImmutableMap<ImmutablePair<Player, Player>, Game> getLatestGames()

}