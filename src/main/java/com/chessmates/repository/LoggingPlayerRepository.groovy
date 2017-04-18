package com.chessmates.repository

import com.chessmates.model.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * Repository that simply logs instead of writing to a DB.
 */
@Repository
class LoggingPlayerRepository implements PlayerRepository {
    Logger logger = LoggerFactory.getLogger(LoggingPlayerRepository)

    @Override
    void save(Player player) {
        logger.debug "PLACEHOLDER: Saving player: ${player}"
    }

    @Override
    Player find(String playerId) {
        throw new UnsupportedOperationException('Reading players isn\'t supported by this implementation.')
    }
}
