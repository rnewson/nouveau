package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.IntPoint;

public class IntPointSerializer extends StdSerializer<IntPoint> {

    public IntPointSerializer() {
        this(null);
    }

    public IntPointSerializer(Class<IntPoint> vc) {
        super(vc);
    }

    @Override
    public void serialize(IntPoint field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "int");
        gen.writeStringField("name", field.name());
        gen.writeNumberField("value", field.numericValue().intValue());
        gen.writeEndObject();
    }

}
