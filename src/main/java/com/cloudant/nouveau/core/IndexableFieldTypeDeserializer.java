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

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.VectorSimilarityFunction;

public class IndexableFieldTypeDeserializer extends StdDeserializer<IndexableFieldType> {

    public IndexableFieldTypeDeserializer() {
        this(null);
    }

    public IndexableFieldTypeDeserializer(Class<IndexableFieldType> vc) {
        super(vc);
    }

    @Override
    public IndexableFieldType deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final FieldType result = new FieldType();

        if (node.has("doc_values_type")) {
            result.setDocValuesType(DocValuesType.valueOf(node.get("doc_values_type").asText()));
        }

        // TODO attributes

        if (node.has("index_options")) {
            result.setIndexOptions(IndexOptions.valueOf(node.get("index_options").asText()));
        }

        if (node.has("omit_norms")) {
            result.setOmitNorms(node.get("omit_norms").asBoolean());
        }

        if (node.has("point_dimension_count")) {
            result.setDimensions(node.get("point_dimension_count").asInt(),
                    node.get("point_index_dimension_count").asInt(), node.get("point_num_bytes").asInt());
        }

        if (node.has("stored")) {
            result.setStored(node.get("stored").asBoolean());
        }

        if (node.has("store_term_vector_offsets")) {
            result.setStoreTermVectorOffsets(node.get("store_term_vector_offsets").asBoolean());
        }

        if (node.has("store_term_vector_positions")) {
            result.setStoreTermVectorPositions(node.get("store_term_vector_positions").asBoolean());
        }

        if (node.has("store_term_vectors")) {
            result.setStoreTermVectors(node.get("store_term_vectors").asBoolean());
        }

        if (node.has("tokenized")) {
            result.setTokenized(node.get("tokenized").asBoolean());
        }

        if (node.has("vector_dimension")) {
            VectorSimilarityFunction vsf = VectorSimilarityFunction.EUCLIDEAN;
            if (node.has("vector_similarity_function")) {
                vsf = VectorSimilarityFunction.valueOf(node.get("vector_similarity_function").asText());
            }
            result.setVectorDimensionsAndSimilarityFunction(node.get("vector_dimensions").asInt(), vsf);
        }

        return result;
    }


}
