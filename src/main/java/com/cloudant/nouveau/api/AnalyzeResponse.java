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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class AnalyzeResponse {

    @NotNull
    private List<@NotEmpty String> tokens;

    @SuppressWarnings("unused")
    public AnalyzeResponse() {
        // Jackson deserialization
    }

    public AnalyzeResponse(List<String> tokens) {
        this.tokens = tokens;
    }

    @JsonProperty
    public List<String> getTokens() {
        return tokens;
    }

}
