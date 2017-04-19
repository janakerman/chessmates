package com.chessmates.repository

import com.chessmates.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * Repository that simply logs instead of writing to a DB.
 */
@Repository
class MockPlayerRepository implements PlayerRepository {
    Logger logger = LoggerFactory.getLogger(MockPlayerRepository)

    private Map<String, Player> store = new HashMap()

    @Override
    void save(Player player) {
        logger.debug "(Mock) Saving player: ${player}"
        store.put(player.id, player)
    }

    @Override
    Player find(String playerId) {
        logger.debug "(Mock) Fetching player: ${playerId}"
        store.get(playerId)
    }

    @Override
    List<Player> findAll() {
        new ArrayList(store.values())
    }
}
