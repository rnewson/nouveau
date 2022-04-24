package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.DoublePoint;

public class DoublePointSerializer extends StdSerializer<DoublePoint> {

    public DoublePointSerializer() {
        this(null);
    }

    public DoublePointSerializer(Class<DoublePoint> vc) {
        super(vc);
    }

    @Override
    public void serialize(DoublePoint field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "double_point");
        gen.writeStringField("name", field.name());
        gen.writeNumberField("value", field.numericValue().doubleValue());
        gen.writeEndObject();
    }

}
