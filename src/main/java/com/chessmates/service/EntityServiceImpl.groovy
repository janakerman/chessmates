package com.chessmates.service

import com.chessmates.lichess.data.LichessDataService
import com.chessmates.model.Game
import com.chessmates.model.Player

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service handling data requests relating to Lichess entities.
 */
@Service
class EntityServiceImpl implements EntityService {

    private LichessDataService lichessDataService

    @Autowired
    EntityServiceImpl(LichessDataService lichessDataService) {
        this.lichessDataService = lichessDataService
    }

    /**
     * Get all Lichess players.
     */
    @Override
    // TODO: Replace with fetch from DB.
    List<Player> getPlayers() { lichessDataService.getPlayers() }

    /**
     * Get all games between scott logic players.
     */
    @Override
    // TODO: Replace with fetch from DB.
    List<Game> getGames() { lichessDataService.getGames(getPlayers()) }

}
