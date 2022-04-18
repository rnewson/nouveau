package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.StoredField;
import org.apache.lucene.util.BytesRef;

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
            gen.writeNumberField("numeric_value", field.numericValue().doubleValue());
        } else if (field.stringValue() != null) {
            gen.writeStringField("string_value", field.stringValue());
        } else if (field.binaryValue() != null) {
            final BytesRef bytesRef = field.binaryValue();
            gen.writeFieldName("binary_value");
            gen.writeBinary(bytesRef.bytes, bytesRef.offset, bytesRef.length);
        }

        gen.writeEndObject();
    }

}
