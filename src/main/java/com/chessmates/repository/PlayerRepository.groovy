package com.chessmates.repository

import com.chessmates.model.Player

/**
 * A repository for Player models.
 */
interface PlayerRepository {

    void save(Player player)
    Player find(String playerId)
    List<Player> findAll()

}