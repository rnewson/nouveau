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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class IndexInfo {

    @NotNull
    private Long updateSeq;

    @NotNull
    private Integer numDocs;

    public IndexInfo() {
    }

    public IndexInfo(final Long updateSeq, final Integer numDocs) {
        this.updateSeq = updateSeq;
        this.numDocs = numDocs;
    }

    @JsonProperty
    public Integer getNumDocs() {
        return numDocs;
    }

    @JsonProperty
    public Long getUpdateSeq() {
        return updateSeq;
    }

    @Override
    public String toString() {
        return "IndexInfo [numDocs=" + numDocs + ", updateSeq=" + updateSeq + "]";
    }

}
