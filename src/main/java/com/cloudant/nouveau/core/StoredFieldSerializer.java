package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.StoredField;

public class StoredFieldSerializer extends StdSerializer<StoredField> {

    public StoredFieldSerializer() {
        this(null);
    }

    public StoredFieldSerializer(Class<StoredField> vc) {
        super(vc);
    }

    @Override
    public void serialize(StoredField field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "stored");
        gen.writeStringField("name", field.name());

        if (field.numericValue() != null) {
            gen.writeNumberField("value", field.numericValue().doubleValue());
        }
        if (field.stringValue() != null) {
            gen.writeStringField("value", field.stringValue());
        }
        if (field.binaryValue() != null) {
            gen.writeObjectField("binary", field.binaryValue());
        }

        gen.writeEndObject();
    }

}
