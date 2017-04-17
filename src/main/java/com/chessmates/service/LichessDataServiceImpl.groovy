package com.chessmates.service

import com.chessmates.model.Game
import com.chessmates.model.Player
import com.chessmates.utility.GetPageFunction
import com.chessmates.utility.LichessApi
import com.chessmates.utility.LichessResultSet
import org.apache.commons.lang3.tuple.ImmutablePair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.stream.Collectors

/**
 * Fetching data from Lichess' REST API.
 */
@Service
class LichessDataServiceImpl implements LichessDataService {

    static final String TEAM_NAME = 'scott-logic'

    private LichessApi lichessApi

    @Autowired
    LichessDataService(LichessApi lichessApi) {
        this.lichessApi = lichessApi
    }

    /**
     * Get new players from the Lichess APi up until the provided player ID. If no latest player is provided, all Lichess
     * players will be fetched
     * @param untilPlayerId If no id is provided, all players will be fetched
     * @return A list of all players.
     */
    @Override
    List<Player> getPlayers(String untilPlayerId) {
        def fetchPlayerPage = { String teamId, int pageNum -> lichessApi.getPlayers(teamId, pageNum) }
        def stopAtPlayerId = { Player player -> player.id == untilPlayerId }

        def resultSet = new LichessResultSet<Player>(
                (GetPageFunction)fetchPlayerPage.curry(TEAM_NAME),
                stopAtPlayerId
        )
        resultSet.get()
    }

    /**
     * Get only new games for each individual player combination.
     *
     * If no map is provided all historical games will be fetched. Go get a coffee!
     *
     * @param players The players to get new games for.
     * @param latestGameMap A map containing the latest game for each pair of opponents.
     * @return A list of all new games.
     */
    @Override
    List<Game> getGames(List<Player> players, Map<ImmutablePair, Game> latestGameMap) {
        latestGameMap = latestGameMap ?: new HashMap<>()

        def fetchGamePage = { Player player, Player opponent, int pageNum -> lichessApi.getGames(player.id, opponent.id, pageNum) }

        def gameIsLatest = { Player player, Player opponent, Game thisGame ->
            thisGame == latestGameMap.get(new ImmutablePair(player, opponent))
        }

        def playerCombinations = uniqueCombinations(players)

        def games = playerCombinations.stream()
        // Get all of the games for a pair of players.
                .map { playerCombination ->

            def player = playerCombination.left
            def opponent = playerCombination.right

            def resultSet = new LichessResultSet<Game>(
                    /* So functional! Bind the fetchGamePage * stop condition with the player arguments. The getAllPages func isn't concerned
                    with any arguments. */
                    /* PS - groovy closures! Why don't you play nice with Java 8 functions?! */
                    (GetPageFunction)fetchGamePage.curry(player, opponent),
                    gameIsLatest.curry(player, opponent),
            )

            def games = resultSet.get()

            if (games.size()) {
                latestGameMap.put(playerCombination, games.first())
            }

            return games
        }
        .flatMap { gamePageResults -> gamePageResults.stream() }
                .collect(Collectors.toList())

        return games
    }

    /**
     * Given an array of objects, return a list of all the unique combination between different objects.
     */
    static private <T> List<ImmutablePair<T, T>> uniqueCombinations(List<T> objects) {
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