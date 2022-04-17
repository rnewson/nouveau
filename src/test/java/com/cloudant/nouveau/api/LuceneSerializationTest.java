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
    public void testSerialization() throws Exception {
        final ObjectMapper om = objectMapper();
        final Document doc = new Document();
        doc.add(new StringField("stringfoo", "bar", Store.YES));
        doc.add(new TextField("textfoo", "hello there", Store.YES));
        doc.add(new DoublePoint("doublefoo", 12));

        final String expected = fixture("fixtures/LuceneFields.json");
        final String actual = om.writeValueAsString(doc);
        assertThat(expected).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testDeserialization() throws Exception {
        final ObjectMapper om = objectMapper();

        final String serialized = fixture("fixtures/LuceneFields.json");
        final Document actual = om.readValue(serialized, Document.class);
        System.err.println(actual);
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
