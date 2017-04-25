package com.chessmates.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * A model object wrapping the Lichess Player response.
 *
 * The purpose of this class is to encapsulate the accessors we use into the returned Lichess data. We don't modify the model
 * returned by lichess, but at the same time we don't want to have to make wide spread changes to our code base should Lichess
 * change their structure.
 */
@JsonSerialize(contentUsing = LichessModelSerializer)
class Player implements LichessModel {

    final Map slurpedJson

    Player(Map slurpedJson) {
        this.slurpedJson = slurpedJson
    }

    static Player createPlayer(Map slurpedJson){
        new Player(slurpedJson)
    }

    String getId() { this.slurpedJson?.id }
    String getUsername() { this.slurpedJson?.username }

}
