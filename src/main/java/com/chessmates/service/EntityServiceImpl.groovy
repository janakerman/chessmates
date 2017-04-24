package com.chessmates.service

import com.chessmates.repository.GameRepository
import com.chessmates.repository.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service handling data requests relating to Lichess entities.
 */
@Service
class EntityServiceImpl implements EntityService {

    private final PlayerRepository playerRepository
    private final GameRepository gameRepository

    @Autowired
    EntityServiceImpl(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository
        this.gameRepository = gameRepository
    }

    /**
     * Get all Lichess players.
     */
    @Override
    List getPlayers() {
        playerRepository.findAll()
    }

    @Override
    getPlayer(String id) {
        playerRepository.find(id)
    }

    /**
     * Get all games between scott logic players.
     */
    @Override
    List getGames() {
        gameRepository.findAll()
    }

    @Override
    getGame(String id) {
        gameRepository.find(id)
    }
}
