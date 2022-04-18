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

import static org.assertj.core.api.Assertions.assertThat;

import com.cloudant.nouveau.core.LuceneModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.XYPointField;
import org.apache.lucene.index.IndexableField;
import org.junit.jupiter.api.Test;

public class LuceneSerializationTest {

    @Test
    public void testSerializationStringField() throws Exception {
        final String str = """
            {
                "type": "string",
                "name": "stringfoo",
                "value": "bar",
                "stored": true
            }
        """;
        roundtripTest(str, new StringField("stringfoo", "bar", Store.YES));
    }

    @Test
    public void testSerializationTextField() throws Exception {
        final String str = """
            {
                "type": "text",
                "name": "textfoo",
                "value": "hello there",
                "stored": true
            }
        """;
        roundtripTest(str, new TextField("textfoo", "hello there", Store.YES));
    }

    @Test
    public void testSerializationDoublePoint() throws Exception {
        final String str = """
                    {
                        "type": "double",
                        "name": "doublefoo",
                        "value": 12.0
                    }
                """;
        roundtripTest(str, new DoublePoint("doublefoo", 12));
    }

    @Test
    public void testSerializationIntPoint() throws Exception {
        final String str = """
                    {
                        "type": "int",
                        "name": "intfoo",
                        "value": 13
                    }
                """;
        roundtripTest(str, new IntPoint("intfoo", 13));
    }

    @Test
    public void testSerializationFloatPoint() throws Exception {
        final String str = """
                    {
                        "type": "float",
                        "name": "floatfoo",
                        "value": 14.5
                    }
                """;
        roundtripTest(str, new FloatPoint("floatfoo", 14.5f));
    }

    @Test
    public void testSerializationLongPoint() throws Exception {
        final String str = """
                    {
                        "type": "long",
                        "name": "longfoo",
                        "value": 15
                    }
                """;
        roundtripTest(str, new LongPoint("longfoo", 15));
    }

    @Test
    public void testSerializationXYPoint() throws Exception {
        final String str = """
            {
                "type": "xy",
                "name": "xyfoo",
                "x": 2.0,
                "y": 4.0
            }
        """;
        roundtripTest(str, new XYPointField("xyfoo", 2, 4));
    }

    private void roundtripTest(final String stringForm, final IndexableField objectForm) throws Exception {
        final ObjectMapper mapper = objectMapper();

        // serialization.
        final String newStringForm = mapper.writeValueAsString(objectForm);
        assertThat(stringForm.replaceAll("\\s","")).isEqualTo(newStringForm.replaceAll("\\s",""));

        // deserialization.
        final Object newObjectForm = mapper.readValue(newStringForm, IndexableField.class);
        assertThat(objectForm.toString()).isEqualToIgnoringWhitespace(newObjectForm.toString());
    }

    private ObjectMapper objectMapper() {
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new LuceneModule());
        return om;
    }

}
