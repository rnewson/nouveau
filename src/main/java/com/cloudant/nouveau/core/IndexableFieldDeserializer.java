package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;

public class IndexableFieldDeserializer extends StdDeserializer<IndexableField> {

    public IndexableFieldDeserializer() {
        this(null);
    }

    public IndexableFieldDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IndexableField deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        final String name = node.get("name").asText();
        final String type = node.get("type").asText();
        boolean stored = false;
        if (node.has("stored")) {
            stored = node.get("stored").asBoolean();
        }
        switch (type) {
        case "string":
            return new StringField(name, node.get("value").asText(), stored ? Store.YES : Store.NO);
        case "text":
            return new TextField(name, node.get("value").asText(), stored ? Store.YES : Store.NO);
        case "double":
            return new DoublePoint(name, node.get("value").asDouble());
        default:
            return null;
        }
    }


}
