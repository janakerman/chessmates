package com.chessmates.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * Serializes the underlying json of a LichessModel.
 */
class LichessModelSerializer extends JsonSerializer<LichessModel> {
    /** Simply returned the wrapped json. */
    @Override
    void serialize(LichessModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.slurpedJson)
    }
}
