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

package com.cloudant.nouveau.core.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LuceneModuleTest {

    private static ObjectMapper mapper;

    @BeforeAll
    public static void setupMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new LuceneModule());
    }

    @Test
    public void testSerializeStringFieldStoreYES() throws Exception {
        final String expected = "{\"@type\":\"string\",\"name\":\"foo\",\"value\":\"bar\",\"stored\":true}";
        final String actual = mapper.writeValueAsString(new StringField("foo", "bar", Store.YES));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeStringFieldStoreNO() throws Exception {
        final String expected = "{\"@type\":\"string\",\"name\":\"foo\",\"value\":\"bar\",\"stored\":false}";
        final String actual = mapper.writeValueAsString(new StringField("foo", "bar", Store.NO));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeTextFieldStoreYES() throws Exception {
        final String expected = "{\"@type\":\"text\",\"name\":\"foo\",\"value\":\"bar\",\"stored\":true}";
        final String actual = mapper.writeValueAsString(new TextField("foo", "bar", Store.YES));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeTextFieldStoreNO() throws Exception {
        final String expected = "{\"@type\":\"text\",\"name\":\"foo\",\"value\":\"bar\",\"stored\":false}";
        final String actual = mapper.writeValueAsString(new TextField("foo", "bar", Store.NO));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeDoublePoint() throws Exception {
        final String expected = "{\"@type\":\"double_point\",\"name\":\"foo\",\"value\":12.5}";
        final String actual = mapper.writeValueAsString(new DoublePoint("foo", 12.5));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeStoredFieldString() throws Exception {
        final String expected = "{\"@type\":\"stored_string\",\"name\":\"foo\",\"value\":\"bar\"}";
        final String actual = mapper.writeValueAsString(new StoredField("foo", "bar"));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeStoredFieldDouble() throws Exception {
        final String expected = "{\"@type\":\"stored_double\",\"name\":\"foo\",\"value\":12.5}";
        final String actual = mapper.writeValueAsString(new StoredField("foo", 12.5));
        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeStoredFieldBinary() throws Exception {
        final String expected = "{\"@type\":\"stored_binary\",\"name\":\"foo\",\"value\":\"YmFy\"}";
        final String actual = mapper.writeValueAsString(new StoredField("foo", new BytesRef("bar")));
        assertEquals(expected, actual);
    }

}
