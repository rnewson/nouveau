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

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.VectorSimilarityFunction;

public class IndexableFieldTypeSerializer extends StdSerializer<IndexableFieldType> {

    public IndexableFieldTypeSerializer() {
        this(null);
    }

    public IndexableFieldTypeSerializer(Class<IndexableFieldType> vc) {
        super(vc);
    }

    @Override
    public void serialize(final IndexableFieldType fieldType, final JsonGenerator gen, final SerializerProvider provider)
        throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("doc_values_type", fieldType.docValuesType().name());
        gen.writeObjectField("attributes", fieldType.getAttributes());
        gen.writeStringField("index_options", fieldType.indexOptions().name());
        gen.writeBooleanField("omit_norms", fieldType.omitNorms());
        gen.writeNumberField("point_dimension_count", fieldType.pointDimensionCount());
        gen.writeNumberField("point_index_dimension_count", fieldType.pointIndexDimensionCount());
        gen.writeNumberField("point_num_bytes", fieldType.pointNumBytes());
        gen.writeBooleanField("stored", fieldType.stored());
        gen.writeBooleanField("store_term_vector_offsets", fieldType.storeTermVectorOffsets());
        gen.writeBooleanField("store_term_vector_payloads", fieldType.storeTermVectorPayloads());
        gen.writeBooleanField("store_term_vector_positions", fieldType.storeTermVectorPositions());
        gen.writeBooleanField("store_term_vectors", fieldType.storeTermVectors());
        gen.writeBooleanField("tokenized", fieldType.tokenized());
        gen.writeNumberField("vector_dimension", fieldType.vectorDimension());
        gen.writeStringField("vector_similarity_function", fieldType.vectorSimilarityFunction().name());
        gen.writeEndObject();
    }

}
