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

import java.util.ArrayList;
import java.util.List;

import com.cloudant.nouveau.core.IndexableFieldDeserializer;
import com.cloudant.nouveau.core.IndexableFieldSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.lucene.document.StringField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;
import org.junit.jupiter.api.Test;

public class DocumentUpdateRequestTest {

    private static final ObjectMapper MAPPER = newObjectMapper();

    @Test
    public void testSerialisation() throws Exception {
        DocumentUpdateRequest request = asObject();
        final String expected = MAPPER.writeValueAsString(
            MAPPER.readValue(fixture("fixtures/DocumentUpdateRequest.json"), DocumentUpdateRequest.class));
        assertThat(MAPPER.writeValueAsString(request)).isEqualTo(expected);
    }

    @Test
    public void testDeserialisation() throws Exception {
        DocumentUpdateRequest request = asObject();
        assertThat(MAPPER.readValue(fixture("fixtures/DocumentUpdateRequest.json"), DocumentUpdateRequest.class))
                .isEqualTo(request);
    }

    private DocumentUpdateRequest asObject() {
        final List<IndexableField> fields = new ArrayList<IndexableField>();
        fields.add(new StringField("stringfoo", "bar", Store.YES));
        fields.add(new TextField("textfoo", "hello there", Store.YES));
        fields.add(new DoublePoint("doublefoo", 12));
        return new DocumentUpdateRequest(12, fields);
    }

    private static ObjectMapper newObjectMapper() {
        final ObjectMapper result = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(IndexableField.class, new IndexableFieldSerializer());
        module.addDeserializer(IndexableField.class, new IndexableFieldDeserializer());
        result.registerModule(module);
        return result;
    }

}
