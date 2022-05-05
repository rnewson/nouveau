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
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.lucene.facet.range.DoubleRange;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class SearchRequest {

    @NotNull
    private String query;

    @Min(1)
    @Max(200)
    private int limit = 25;

    private List<@NotEmpty String> sort;

    private List<@NotEmpty String> counts;

    private Map<@NotEmpty String, List<@NotNull DoubleRange>> ranges;

    @Min(1)
    @Max(100)
    private int topN = 10;

    @SuppressWarnings("unused")
    public SearchRequest() {
        // Jackson deserialization
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    @JsonProperty
    public String getQuery() {
        return query;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    @JsonProperty
    public int getLimit() {
        return limit;
    }

    public boolean hasSort() {
        return sort != null;
    }

    @JsonProperty
    public List<String> getSort() {
        return sort;
    }

    public boolean hasCounts() {
        return counts != null;
    }

    public void setCounts(final List<String> counts) {
        this.counts = counts;
    }

    @JsonProperty
    public List<String> getCounts() {
        return counts;
    }

    public boolean hasRanges() {
        return ranges != null;
    }

    public void setRanges(final Map<String, List<DoubleRange>> ranges) {
        this.ranges = ranges;
    }

    @JsonProperty
    public Map<String, List<DoubleRange>> getRanges() {
        return ranges;
    }

    @JsonProperty
    public void setTopN(final int topN) {
        this.topN = topN;
    }

    public int getTopN() {
        return topN;
    }

    @Override
    public String toString() {
        return "SearchRequest [query=" + query + ", sort=" + sort + ", limit=" + limit + ", counts=" + counts + ", ranges=" + ranges + "]";
    }

}
