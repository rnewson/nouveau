package com.cloudant.nouveau.core;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.VectorSimilarityFunction;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
@JsonInclude(Include.NON_DEFAULT)
public abstract class FieldTypeMixin {

    @JsonProperty
    private boolean stored;

    @JsonProperty
    private boolean tokenized;

    @JsonProperty
    private boolean storeTermVectors;

    @JsonProperty
    private boolean storeTermVectorOffsets;

    @JsonProperty
    private boolean storeTermVectorPositions;

    @JsonProperty
    private boolean storeTermVectorPayloads;

    @JsonProperty
    private boolean omitNorms;

    @JsonProperty
    private IndexOptions indexOptions;

    @JsonProperty
    private DocValuesType docValuesType;

    @JsonProperty
    private int dimensionCount;

    @JsonProperty
    private int indexDimensionCount;

    @JsonProperty
    private int dimensionNumBytes;

    @JsonProperty
    private int vectorDimension;

    @JsonProperty
    private VectorSimilarityFunction vectorSimilarityFunction;

    @JsonProperty
    private Map<String, String> attributes;

}
