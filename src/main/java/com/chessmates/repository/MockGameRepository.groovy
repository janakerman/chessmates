package com.chessmates.repository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * Repository that simply logs instead of writing to a DB.
 */
@Repository
class MockGameRepository implements GameRepository {
    Logger logger = LoggerFactory.getLogger(MockGameRepository)

    Map store = new HashMap()

    @Override
    void save(game) {
        logger.debug "(Mock) Saving game: ${game}"
        store.put(game.id, game)
    }

    @Override
    find(String gameId) {
        logger.debug "(Mock) Fetching game: ${gameId}"
        store.get(gameId)
    }

    @Override
    List findAll() {
        new ArrayList(store.values())
    }
}
