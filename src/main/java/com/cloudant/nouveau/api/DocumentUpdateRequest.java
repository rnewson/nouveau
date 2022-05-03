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

import java.util.Collection;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.lucene.index.IndexableField;

import io.dropwizard.jackson.JsonSnakeCase;

@JsonSnakeCase
public class DocumentUpdateRequest {

    @Min(1)
    private long seq;

    @NotEmpty
    private Collection<@NotNull IndexableField> fields;

    @SuppressWarnings("unused")
    public DocumentUpdateRequest() {
        // Jackson deserialization
    }

    public DocumentUpdateRequest(long seq, Collection<IndexableField> fields) {
        this.seq = seq;
        this.fields = fields;
    }

    @JsonProperty
    public long getSeq() {
        return seq;
    }

    @JsonProperty
    public Collection<IndexableField> getFields() {
        return fields;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + (int) (seq ^ (seq >>> 32));
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
        DocumentUpdateRequest other = (DocumentUpdateRequest) obj;
        if (fields == null) {
            if (other.fields != null)
                return false;
        } else if (!fields.equals(other.fields))
            return false;
        if (seq != other.seq)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DocumentUpdateRequest [fields=" + fields + ", seq=" + seq + "]";
    }

}
