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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

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

        gen.writeObjectField("type", field.fieldType());

        if (field.stringValue() != null) {
            gen.writeStringField("string_value", field.stringValue());
        }

        if (field.binaryValue() != null) {
            final BytesRef bytesRef = field.binaryValue();
            gen.writeFieldName("binary_value");
            gen.writeBinary(bytesRef.bytes, bytesRef.offset, bytesRef.length);
        }

        gen.writeEndObject();
    }

}
