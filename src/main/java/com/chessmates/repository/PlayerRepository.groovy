package com.chessmates.repository

import com.chessmates.model.Player

/**
 * Created by jakerman on 18/04/2017.
 */
interface PlayerRepository {

    void save(Player player)
    Player find(String playerId)
}