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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonInclude(Include.NON_DEFAULT)
@JsonSnakeCase
public class DoubleField implements Field {

    @NotEmpty
    private String name;

    @NotNull
    private Double value;

    private boolean stored;

    private boolean facet;

    public DoubleField() {
    }

    public DoubleField(String name, double value, boolean stored, boolean facet) {
        this.name = name;
        this.value = value;
        this.stored = stored;
        this.facet = facet;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Double getValue() {
        return value;
    }

    @JsonProperty
    public boolean isStored() {
        return stored;
    }

    @JsonProperty
    public boolean isFacet() {
        return facet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (facet ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (stored ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoubleField other = (DoubleField) obj;
        if (facet != other.facet)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (stored != other.stored)
            return false;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DoubleField [facet=" + facet + ", name=" + name + ", stored=" + stored + ", value=" + value + "]";
    }

}
