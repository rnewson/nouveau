package com.cloudant.nouveau.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.apache.lucene.index.IndexableFieldType;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type")
public abstract class FieldMixin {

    @JsonProperty
    private String name;

    @JsonProperty
    private IndexableFieldType type;

    @JsonProperty("value")
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type")
    private Object fieldsData;

}
