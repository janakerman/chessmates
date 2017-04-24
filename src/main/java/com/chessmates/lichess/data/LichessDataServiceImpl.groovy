package com.chessmates.lichess.data

import com.chessmates.repository.GameRepository
import com.chessmates.repository.MetaDataRepository
import com.chessmates.repository.PlayerRepository
import org.apache.commons.lang3.tuple.ImmutablePair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.util.stream.Collectors

/**
 * Fetching data from Lichess' REST API.
 */
@Service
class LichessDataServiceImpl implements LichessDataService {

    static final String TEAM_NAME = 'scott-logic'

    @Value('${chessmates.lichess.api.user.pageSize}')
    private Integer pageSizePlayers

    @Value('${chessmates.lichess.api.game.pageSize}')
    private Integer pageSizeGames

    private LichessApi lichessApi
    private PlayerRepository playerRepository
    private GameRepository gameRepository
    private MetaDataRepository metaDataRepository

    @Autowired
    LichessDataService(LichessApi lichessApi, PlayerRepository playerRepository, GameRepository gameRepository, MetaDataRepository metaDataRepository) {
        this.lichessApi = lichessApi
        this.playerRepository = playerRepository
        this.gameRepository = gameRepository
        this.metaDataRepository = metaDataRepository
    }

    /**
     * Get new players from the Lichess APi up until the provided player ID. If no latest player is provided, all Lichess
     * players will be fetched
     * @param untilPlayerId If no id is provided, all players will be fetched
     * @return A list of all players.
     */
    @Override
    List updatePlayers() {
        final untilPlayer = metaDataRepository.getLatestPlayer()
        final fetchPlayerPage = { String teamId, int pageNum -> lichessApi.getPlayersPage(teamId, pageNum, pageSizePlayers) }
        final stopAtPlayerId = { player ->
            if (!untilPlayer) false
            else  player.id == untilPlayer?.id
        }

        final resultSet = new LichessResultSet(
                fetchPlayerPage.curry(TEAM_NAME),
                stopAtPlayerId
        )

        List players = resultSet.get()
                .stream()
                .filter(LichessEntityValidator.&isValid)
                .collect(Collectors.toList())

        players.each(playerRepository.&save)
        if (players.size()) {
            metaDataRepository.saveLatestPlayer(players.first())
        }

        return players
    }

    /**
     * Get only new games for each individual player combination.
     *
     * If no store is provided all historical games will be fetched. Go get a coffee!
     *
     * @param players The players to get new games for.
     * @param latestGameMap A store containing the latest game for each pair of opponents.
     * @return A list of all new games.
     */
    @Override
    List updateGames(List players) {

        final latestGameMap = metaDataRepository.getLatestGames()

        def fetchGamePage = { player, opponent, int pageNum ->
            lichessApi.getGamesPage(player.id, opponent.id, pageNum, pageSizeGames) }

        def gameIsLatest = { player, opponent, thisGame ->
            def latestGameForOpponents = latestGameMap.get(new ImmutablePair(player, opponent))
            if (!latestGameForOpponents) return false
            else thisGame.id == latestGameForOpponents?.id
        }

        def playerCombinations = uniqueCombinations(players)

        playerCombinations.stream()
        // Get all of the games for a pair of players.
            .map { playerCombination ->

                def player = playerCombination.left
                def opponent = playerCombination.right

                def resultSet = new LichessResultSet(
                        /* So functional! Bind the fetchGamePage * stop condition with the player arguments. The getAllPages func isn't concerned
                        with any arguments. */
                        /* PS - groovy closures! Why don't you play nice with Java 8 functions?! */
                        fetchGamePage.curry(player, opponent),
                        gameIsLatest.curry(player, opponent),
                )

                List games = resultSet.get()
                        .stream()
                        .filter(LichessEntityValidator.&isValid)
                        .collect(Collectors.toList())

                games.each(gameRepository.&save)
                if (games.size()) {
                    metaDataRepository.saveLatestGame(player, opponent, games.first())
                }

                return games
            }
            .flatMap { gamePageResults -> gamePageResults.stream() }
            .collect(Collectors.toList())
    }

    /**
     * Given an array of objects, return a list of all the unique combination between different objects.
     */
    static private <T> List<ImmutablePair<T, T>> uniqueCombinations(List<T> objects) {
        if (!objects) {
            return []
        }

        def matchingIndex = 1
        def combinations = []
        def numPlayers = objects.size()

        for (def playerIndex = 0; playerIndex<numPlayers; playerIndex++) {
            for (def opponentIndex = matchingIndex; opponentIndex<numPlayers; opponentIndex++) {

                def player = objects.get(playerIndex)
                def opponent = objects.get(opponentIndex)

                combinations.add(new ImmutablePair(player, opponent))
            }
            matchingIndex++
        }
        return combinations
    }

}
