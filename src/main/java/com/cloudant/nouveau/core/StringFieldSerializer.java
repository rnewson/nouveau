package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.StringField;

public class StringFieldSerializer extends StdSerializer<StringField> {

    public StringFieldSerializer() {
        this(null);
    }

    public StringFieldSerializer(Class<StringField> vc) {
        super(vc);
    }

    @Override
    public void serialize(StringField field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "string");
        gen.writeStringField("name", field.name());
        gen.writeStringField("value", field.stringValue());
        gen.writeBooleanField("stored", field.fieldType().stored());
        gen.writeEndObject();
    }

}
