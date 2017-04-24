package com.chessmates.lichess.data

import com.chessmates.service.EntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduled tasks that hit the Lichess API for data.
 */
@Component
@ConditionalOnProperty(value = 'chessmates.lichess.api.dataupdate', havingValue = 'true', matchIfMissing = false)
class LichessDataScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LichessDataScheduler)

    LichessDataService lichessDataService
    EntityService entityService

    @Autowired
    LichessDataScheduler(LichessDataService lichessDataService, EntityService entityService) {
        this.lichessDataService = lichessDataService
        this.entityService = entityService
    }

    @Scheduled(initialDelay = 0L, fixedDelay = 3600000L)
    void updateData() {
        final startTime = new Date()
        logger.debug "Starting Lichess data update: ${startTime} (${startTime.getTime()})"

        // TODO: Rename these functions, they are saving as a side affect and that isn't clear.
        lichessDataService.updatePlayers()

        final players = entityService.getPlayers()
        lichessDataService.updateGames(players)

        final endTime = new Date()
        logger.debug "Finished Lichess data update: ${endTime} (${endTime.getTime()})"
    }

}
