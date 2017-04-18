package com.chessmates.lichess.data

import com.chessmates.model.Game
import com.chessmates.model.Player
import com.chessmates.repository.GameRepository
import com.chessmates.repository.PlayerRepository
import com.chessmates.utility.HttpUtility
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.apache.commons.lang3.tuple.ImmutablePair
import org.spockframework.spring.ScanScopedBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject
import spock.mock.DetachedMockFactory

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
@ScanScopedBeans
class LichessDataServiceImplTest extends Specification {

    @TestConfiguration
    private static class MockConfig {

        final detatchedMockFactory = new DetachedMockFactory()

        @Bean
        HttpUtility httpUtility() {
            detatchedMockFactory.Mock(HttpUtility)
        }

        @Bean
        PlayerRepository playerRepository() {
            detatchedMockFactory.Mock(PlayerRepository)
        }

        @Bean
        GameRepository gameRepository() {
            detatchedMockFactory.Mock(GameRepository)
        }

    }

    private static class Helper {

        static class MockEndpointInfo {
            String url
            String responseFile
        }

        static final PAGE_SIZE_GAMES = 10
        static final PAGE_SIZE_USERS = 5

        static final TEAM_WITH_EMPTY_USERS = new MockEndpointInfo(
                url: "https://en.lichess.org/api/user?team=scott-logic&nb=${PAGE_SIZE_USERS}&page=1",
                responseFile: 'scott-logic-users-empty.json'
        )
        static final TEAM_WITH_INVALID_USERS = new MockEndpointInfo(
                url: "https://en.lichess.org/api/user?team=scott-logic&nb=${PAGE_SIZE_USERS}&page=1",
                responseFile: 'scott-logic-users-invalid.json'
        )
        static final SCOTT_LOGIC_TEAM_1 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/user?team=scott-logic&nb=${PAGE_SIZE_USERS}&page=1",
                responseFile: 'scott-logic-users-1.json'
        )
        static final SCOTT_LOGIC_TEAM_2 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/user?team=scott-logic&nb=${PAGE_SIZE_USERS}&page=2",
                responseFile: 'scott-logic-users-2.json'
        )
        static final TF235_VS_OWENNW_INVALID = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/tf235/owennw?nb=${PAGE_SIZE_GAMES}&page=1",
                responseFile: 'tf235-vs-owennw-invalid.json'
        )
        static final TF235_VS_OWENNW_1 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/tf235/owennw?nb=${PAGE_SIZE_GAMES}&page=1",
                responseFile: 'tf235-vs-owennw-1.json'
        )
        static final TF235_VS_OWENNW_2 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/tf235/owennw?nb=${PAGE_SIZE_GAMES}&page=2",
                responseFile: 'tf235-vs-owennw-2.json'
        )
        static final TF235_VS_JEDRUS07_1 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/tf235/jedrus07?nb=${PAGE_SIZE_GAMES}&page=1",
                responseFile: 'tf235-vs-jedrus07-1.json'
        )
        static final TF235_VS_JEDRUS07_2 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/tf235/jedrus07?nb=${PAGE_SIZE_GAMES}&page=2",
                responseFile: 'tf235-vs-jedrus07-2.json'
        )
        static final OWENNW_VS_JEDRUS07_1 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/owennw/jedrus07?nb=${PAGE_SIZE_GAMES}&page=1",
                responseFile: 'owennw-vs-jedrus07-1.json'
        )
        static final OWENNW_VS_JEDRUS07_2 = new MockEndpointInfo(
                url: "https://en.lichess.org/api/games/vs/owennw/jedrus07?nb=${PAGE_SIZE_GAMES}&page=2",
                responseFile: 'owennw-vs-jedrus07-2.json'
        )

        static String loadFile(String fileName) {
            Resources.toString(Resources.getResource(fileName), Charsets.UTF_8)
        }

    }

    @Subject
    @Autowired
    LichessDataServiceImpl service

    @Autowired
    HttpUtility httpUtility

    @Autowired
    PlayerRepository playerRepository

    @Autowired
    GameRepository gameRepository

    def setup() {
        // Set smaller page sizes as we provide data for these pages sizes.
        ReflectionTestUtils.setField(service, 'pageSizePlayers', Helper.PAGE_SIZE_USERS)
        ReflectionTestUtils.setField(service, 'pageSizeGames', Helper.PAGE_SIZE_GAMES)
    }

    // Player tests.

    def "ignores invalid players"() {
        when:
        final players = service.getPlayers(null)

        then:
        httpUtility.get(Helper.TEAM_WITH_INVALID_USERS.url) >> Helper.loadFile(Helper.TEAM_WITH_INVALID_USERS.responseFile)

        players.size() == 1
    }

    def "returns empty list of players with empty data set"() {
        when:
        final players = service.getPlayers(null)

        then:
        httpUtility.get(Helper.TEAM_WITH_EMPTY_USERS.url) >> Helper.loadFile(Helper.TEAM_WITH_EMPTY_USERS.responseFile)
        players.size() == 0
    }

    def "returns full set of players when no latest player is provided"() {
        given:
        final latestPlayer = null

        when:
        final players = service.getPlayers(latestPlayer)

        then:
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_1.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_1.responseFile)
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_2.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_2.responseFile)

        players.size() == 8
        players[0].id == 'jfaker'
        players[1].id == 'riciardos'
        players[2].id == 'samei07'
        players[3].id == 'sydeman'
        players[4].id == 'torrlane'
        players[5].id == 'tf235'
        players[6].id == 'owennw'
        players[7].id == 'jedrus07'
    }

    def "stops fetching at provided player when latest player is provided"() {
        given:
        final latestPlayer = 'tf235'

        when:
        final players = service.getPlayers(latestPlayer)

        then:
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_1.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_1.responseFile)
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_2.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_2.responseFile)

        players.size() == 5
        players[0].id == 'jfaker'
        players[1].id == 'riciardos'
        players[2].id == 'samei07'
        players[3].id == 'sydeman'
        players[4].id == 'torrlane'
    }

    def "saves players when new player is fetched"() {
        given:
        final latestPlayer = 'tf235'

        when:
        service.getPlayers(latestPlayer)

        then:
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_1.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_1.responseFile)
        httpUtility.get(Helper.SCOTT_LOGIC_TEAM_2.url) >> Helper.loadFile(Helper.SCOTT_LOGIC_TEAM_2.responseFile)

        1 * playerRepository.save(new Player('jfaker', null))
        1 * playerRepository.save(new Player('riciardos', null))
        1 * playerRepository.save(new Player('samei07', null))
        1 * playerRepository.save(new Player('sydeman', null))
        1 * playerRepository.save(new Player('torrlane', null))
    }

    // Games tests.

    def "ignores invalid games"() {
        given:
        final latestGames = null
        final players = [
                new Player('tf235', 'tf235'),
                new Player('owennw', 'owennw'),
        ]

        when:
        final games = service.getGames(players, latestGames)

        then:
        httpUtility.get(Helper.TF235_VS_OWENNW_INVALID.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_INVALID.responseFile)
        games.size() == 1
    }

    def "returns empty games when no players are provided"() {
        given:
        final players = null
        final latestGames = null

        when:
        final games = service.getGames(players, latestGames)

        then:
        games.size() == 0
    }

    def "returns full set of games when no latest games are provided"() {
        given:
        final latestGames = null
        final players = [
                new Player('tf235', 'tf235'),
                new Player('owennw', 'owennw'),
                new Player('jedrus07', 'jedrus07')
        ]

        when:
        final games = service.getGames(players, latestGames)

        then:
        httpUtility.get(Helper.TF235_VS_OWENNW_1.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_1.responseFile)
        httpUtility.get(Helper.TF235_VS_OWENNW_2.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_2.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_2.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_2.responseFile)
        games.size() == 56
    }

    def "stops fetching at provided game when latest games are provided"() {
        given:
        final players = [
                new Player('tf235', 'tf235'),
                new Player('owennw', 'owennw'),
                new Player('jedrus07', 'jedrus07')
        ]
        final latestGames = new HashMap()
        latestGames.put(new ImmutablePair(players[0], players[1]), new Game('tlicb8yX', null))
        latestGames.put(new ImmutablePair(players[0], players[2]), new Game('ThSBEyjg', null))
        latestGames.put(new ImmutablePair(players[1], players[2]), new Game('0Qk6CAqq', null))

        when:
        final games = service.getGames(players, latestGames)

        then:
        httpUtility.get(Helper.TF235_VS_OWENNW_1.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_1.responseFile)
        httpUtility.get(Helper.TF235_VS_OWENNW_2.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_2.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_2.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_2.responseFile)
        games.size() == 18
    }

    def "saves game when new game is fetched"() {
        given:
        final players = [
                new Player('tf235', 'tf235'),
                new Player('owennw', 'owennw'),
                new Player('jedrus07', 'jedrus07')
        ]
        final latestGames = null

        when:
        service.getGames(players, latestGames)

        then:
        httpUtility.get(Helper.TF235_VS_OWENNW_1.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_1.responseFile)
        httpUtility.get(Helper.TF235_VS_OWENNW_2.url) >> Helper.loadFile(Helper.TF235_VS_OWENNW_2.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.TF235_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.TF235_VS_JEDRUS07_2.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_1.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_1.responseFile)
        httpUtility.get(Helper.OWENNW_VS_JEDRUS07_2.url) >> Helper.loadFile(Helper.OWENNW_VS_JEDRUS07_2.responseFile)
        56 * gameRepository.save(_ as Game)
    }

}
