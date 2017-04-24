package com.chessmates.service
/**
 * Service handling data requests relating to Lichess entities.
 */
interface EntityService {

    /**
     * Returns a list of team players.
     */
    List getPlayers()

    /**
     * Return a player of a given ID.
     */
    def getPlayer(String id)

    /**
     * Returns a list of games played between team players.
     */
    List getGames()

    /**
     * Return a game of a given ID.
     */
    def getGame(String id)

}