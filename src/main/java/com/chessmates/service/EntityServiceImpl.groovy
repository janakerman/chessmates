package com.chessmates.service

import com.chessmates.lichess.data.LichessDataService
import com.chessmates.model.Game
import com.chessmates.model.Player
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
    List<Player> getPlayers() { playerRepository.findAll() }

    /**
     * Get all games between scott logic players.
     */
    @Override
    List<Game> getGames() { gameRepository.findAll() }

}
