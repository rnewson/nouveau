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

package com.cloudant.nouveau.api;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.cloudant.nouveau.core.IndexableFieldDeserializer;
import com.cloudant.nouveau.core.IndexableFieldSerializer;
import com.cloudant.nouveau.core.IndexableFieldTypeDeserializer;
import com.cloudant.nouveau.core.IndexableFieldTypeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.junit.jupiter.api.Test;

public class LuceneSerializationTest {

    @Test
    public void testSerializationStringField() throws Exception {
        final String str = """
            {
                "name": "stringfoo",
                "type": {
                    "index_options": "DOCS",
                    "omit_norms": true,
                    "stored": true,
                    "tokenized": false
                },
                "string_value": "bar"
            }
        """;
        roundtripTest(str, new StringField("stringfoo", "bar", Store.YES));
    }

    @Test
    public void testSerializationTextField() throws Exception {
        final String str = """
            {
                "name": "textfoo",
                "type": {
                    "index_options": "DOCS_AND_FREQS_AND_POSITIONS",
                    "stored": true
                },
                "string_value": "hello there"
            }
        """;
        roundtripTest(str, new TextField("textfoo", "hello there", Store.YES));
    }

    @Test
    public void testSerializationDoublePoint() throws Exception {
        final String str = """
            {
                "name": "doublefoo",
                "type": {
                    "point_dimension_count": 1,
                    "point_index_dimension_count": 1,
                    "point_num_bytes": 8
                },
                "binary_value": "wCgAAAAAAAA="
            }
        """;
        roundtripTest(str, new DoublePoint("doublefoo", 12));
    }

    private void roundtripTest(final String stringForm, final IndexableField objectForm) throws Exception {
        final ObjectMapper mapper = objectMapper();

        // serialization.
        final String newStringForm = mapper.writeValueAsString(objectForm);
        assertThat(stringForm).isEqualToIgnoringWhitespace(newStringForm);

        // deserialization.
        final Object newObjectForm = mapper.readValue(newStringForm, IndexableField.class);
        assertThat(objectForm.toString()).isEqualToIgnoringWhitespace(newObjectForm.toString());
    }

    private ObjectMapper objectMapper() {
        final ObjectMapper om = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(IndexableField.class, new IndexableFieldSerializer());
        module.addSerializer(IndexableFieldType.class, new IndexableFieldTypeSerializer());
        module.addDeserializer(IndexableField.class, new IndexableFieldDeserializer());
        module.addDeserializer(IndexableFieldType.class, new IndexableFieldTypeDeserializer());
        om.registerModule(module);
        return om;
    }

}
