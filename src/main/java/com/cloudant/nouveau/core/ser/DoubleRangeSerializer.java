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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.facet.range.DoubleRange;

class DoubleRangeSerializer extends StdSerializer<DoubleRange> {

    public DoubleRangeSerializer() {
        this(null);
    }

    public DoubleRangeSerializer(Class<DoubleRange> vc) {
        super(vc);
    }

    @Override
    public void serialize(final DoubleRange doubleRange, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("label", doubleRange.label);
        gen.writeNumberField("min", doubleRange.min);
        gen.writeNumberField("max", doubleRange.max);
        gen.writeEndObject();
    }

}
