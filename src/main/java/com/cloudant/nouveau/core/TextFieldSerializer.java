package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.TextField;

public class TextFieldSerializer extends StdSerializer<TextField> {

    public TextFieldSerializer() {
        this(null);
    }

    public TextFieldSerializer(Class<TextField> vc) {
        super(vc);
    }

    @Override
    public void serialize(TextField field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "text");
        gen.writeStringField("name", field.name());
        gen.writeStringField("value", field.stringValue());
        gen.writeBooleanField("stored", field.fieldType().stored());
        gen.writeEndObject();
    }

}
