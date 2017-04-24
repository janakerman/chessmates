package com.chessmates.model

/**
 * A lichess model object.
 *
 * All lichess model objects are just wrappers around the slurped JSON structure.
 */
interface LichessModel {
    /** The underlying slurped JSON structure. */
    Map slurpedJson
}
