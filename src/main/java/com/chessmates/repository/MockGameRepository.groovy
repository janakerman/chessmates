package com.chessmates.repository

import com.chessmates.model.Game
import com.chessmates.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * Repository that simply logs instead of writing to a DB.
 */
@Repository
class MockGameRepository implements GameRepository {
    Logger logger = LoggerFactory.getLogger(MockGameRepository)

    Map<String, Game> store = new HashMap()

    @Override
    void save(Game game) {
        logger.debug "(Mock) Saving game: ${game}"
        store.put(game.id, game)
    }

    @Override
    Game find(String gameId) {
        logger.debug "(Mock) Fetching game: ${gameId}"
        store.get(gameId)
    }

    @Override
    List<Player> findAll() {
        new ArrayList(store.values())
    }
}
