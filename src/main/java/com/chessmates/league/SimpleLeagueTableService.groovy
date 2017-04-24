package com.chessmates.league

import com.chessmates.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service responsible for creating league tables.
 */
@Service
class SimpleLeagueTableService implements LeagueTableService {

    private final Float WIN_POINTS = 1
    private final Float DRAW_POINTS = 0.5
    private final Float LOOSE_POINTS = 0

    private final GameRepository gameRepository

    @Autowired
    SimpleLeagueTableService(GameRepository gameRepository) {
        this.gameRepository = gameRepository
    }

    @Override
    LeagueTable getTable() {
        final table = new LeagueTable(WIN_POINTS, DRAW_POINTS, LOOSE_POINTS)

        final allGames = gameRepository.findAll()
        table.add allGames

        return table
    }
}
