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

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class SearchHit {

    @NotEmpty
    private String id;

    @NotNull
    private List<Object> order;

    @NotNull
    private List<Field> fields;

    public SearchHit() {
    }

    public SearchHit(final String id, final List<Object> order, final List<Field> fields) {
        this.id = id;
        this.order = order;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public List<Object> getOrder() {
        return order;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "SearchHit [id=" + id + ", order=" + order + ", fields=" + fields + "]";
    }

}
