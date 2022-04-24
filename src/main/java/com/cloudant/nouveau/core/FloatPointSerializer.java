package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.FloatPoint;

public class FloatPointSerializer extends StdSerializer<FloatPoint> {

    public FloatPointSerializer() {
        this(null);
    }

    public FloatPointSerializer(Class<FloatPoint> vc) {
        super(vc);
    }

    @Override
    public void serialize(FloatPoint field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "float_point");
        gen.writeStringField("name", field.name());
        gen.writeNumberField("value", field.numericValue().floatValue());
        gen.writeEndObject();
    }

}
