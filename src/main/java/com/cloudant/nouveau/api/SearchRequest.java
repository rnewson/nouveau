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

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.lucene.search.Sort;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class SearchRequest {

    @NotNull
    private String query;

    @Min(1)
    @Max(200)
    private int limit = 25;

    private Sort sort;

    @SuppressWarnings("unused")
    public SearchRequest() {
        // Jackson deserialization
    }

    @JsonProperty
    public String getQuery() {
        return query;
    }

    @JsonProperty
    public int getLimit() {
        return limit;
    }

    public boolean hasSort() {
        return sort != null;
    }

    @JsonProperty
    public Sort getSort() {
        return sort;
    }

    @Override
    public String toString() {
        return "SearchRequest [limit=" + limit + ", query=" + query + ", sort=" + sort + "]";
    }

}
