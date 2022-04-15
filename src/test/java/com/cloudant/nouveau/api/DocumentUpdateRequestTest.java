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

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

public class DocumentUpdateRequestTest {

    @Test
    public void testSerialisation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(asString(), mapper.writeValueAsString(asObject()));
    }

    @Test
    public void testDeserialisation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(asObject(), mapper.readValue(asString(), DocumentUpdateRequest.class));
    }

    private String asString() {
        return "{\"seq\":12,\"fields\":[{\"@type\":\"string\",\"name\":\"stringfoo\",\"value\":\"bar\",\"stored\":true,\"facet\":true},{\"@type\":\"text\",\"name\":\"textfoo\",\"value\":\"hello there\",\"stored\":true},{\"@type\":\"double\",\"name\":\"doublefoo\",\"value\":12.0,\"stored\":true,\"facet\":true}]}";
    }

    private DocumentUpdateRequest asObject() {
        final List<Field> fields = new ArrayList<Field>();
        fields.add(new StringField("stringfoo", "bar", true, true));
        fields.add(new TextField("textfoo", "hello there", true));
        fields.add(new DoubleField("doublefoo", 12, true, true));
        return new DocumentUpdateRequest(12, fields);
    }

}
