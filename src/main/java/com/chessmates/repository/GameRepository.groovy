package com.chessmates.repository

import com.chessmates.model.Game

/**
 * Repository for game models.
 */
interface GameRepository {
    void save(Game player)
    Game find(String playerId)
}