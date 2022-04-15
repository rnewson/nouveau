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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonInclude(Include.NON_DEFAULT)
@JsonSnakeCase
public class TextField implements Field {

    @NotEmpty
    private String name;

    @NotEmpty
    private String value;

    private boolean stored;

    public TextField() {
    }

    public TextField(String name, String value, boolean stored) {
        this.name = name;
        this.value = value;
        this.stored = stored;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getValue() {
        return value;
    }

    @JsonProperty
    public boolean isStored() {
        return stored;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (stored ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        TextField other = (TextField) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (stored != other.stored)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TextField [name=" + name + ", stored=" + stored + ", value=" + value + "]";
    }

}
