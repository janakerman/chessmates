package com.chessmates.repository

import com.chessmates.model.Game
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * Repository that simply logs instead of writing to a DB.
 */
@Repository
class LoggingGameRepository implements GameRepository {
    Logger logger = LoggerFactory.getLogger(LoggingGameRepository)

    @Override
    void save(Game game) {
        logger.debug "PLACEHOLDER: Saving game: ${game}"
    }

    @Override
    Game find(String playerId) {
        throw new UnsupportedOperationException('Reading games isn\'t supported by this implementation.')
    }
}
