// Copyright 2022 Robert Newson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.XYPointField;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

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

        switch (type) {
        case "double_point":
            return new DoublePoint(name, node.get("value").doubleValue());
        case "float_point":
            return new FloatPoint(name, node.get("value").floatValue());
        case "int_point":
            return new IntPoint(name, node.get("value").intValue());
        case "long_point":
            return new LongPoint(name, node.get("value").longValue());
        case "xy_point":
            return new XYPointField(name, node.get("x").floatValue(), node.get("y").floatValue());
        case "string":
            return new StringField(name, node.get("value").asText(),
                    node.get("stored").asBoolean() ? Store.YES : Store.NO);
        case "text":
            return new TextField(name, node.get("value").asText(),
                    node.get("stored").asBoolean() ? Store.YES : Store.NO);
        case "stored_double":
            return new StoredField(name, node.get("value").asDouble());
        case "stored_string":
                return new StoredField(name, node.get("value").asText());
        case "stored_binary":
            return new StoredField(name, node.get("value").binaryValue());
        case "sorted_set_dv":
            return new SortedSetDocValuesFacetField(name, node.get("value").asText());
        case "sorted_dv":
            return new SortedDocValuesField(name, new BytesRef(node.get("value").binaryValue()));
        case "double_dv":
            return new DoubleDocValuesField(name, node.get("value").asDouble());
        }
        throw new IOException(type + " not a valid type of field");
    }

}
