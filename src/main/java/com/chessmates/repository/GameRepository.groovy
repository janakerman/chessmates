package com.chessmates.repository
/**
 * Repository for Game models.
 */
interface GameRepository {
    void save(game)
    def find(String gameId)
    List findAll()
}