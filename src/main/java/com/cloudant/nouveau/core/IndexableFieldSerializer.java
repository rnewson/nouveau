package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public class IndexableFieldSerializer extends StdSerializer<IndexableField> {

    public IndexableFieldSerializer() {
        this(null);
    }

    public IndexableFieldSerializer(Class<IndexableField> vc) {
        super(vc);
    }

    @Override
    public void serialize(final IndexableField field, final JsonGenerator gen, final SerializerProvider provider)
        throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("name", field.name());
        gen.writeBooleanField("stored", field.fieldType().stored());

        if (field instanceof StringField) {
            gen.writeStringField("name", "string");
            gen.writeStringField("value", field.stringValue());
        }

        if (field instanceof TextField) {
            gen.writeStringField("name", "string");
            gen.writeStringField("value", field.stringValue());
        }

        if (field instanceof DoublePoint) {
            gen.writeStringField("name", "double");
            gen.writeNumberField("value", (Double) field.numericValue());
        }

        gen.writeEndObject();
    }

}
