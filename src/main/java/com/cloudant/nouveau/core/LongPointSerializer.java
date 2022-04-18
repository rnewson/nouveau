package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.LongPoint;

public class LongPointSerializer extends StdSerializer<LongPoint> {

    public LongPointSerializer() {
        this(null);
    }

    public LongPointSerializer(Class<LongPoint> vc) {
        super(vc);
    }

    @Override
    public void serialize(LongPoint field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "long");
        gen.writeStringField("name", field.name());
        gen.writeNumberField("value", field.numericValue().longValue());
        gen.writeEndObject();
    }

}
