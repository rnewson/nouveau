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

package com.cloudant.nouveau.core;

import java.io.IOException;

import com.cloudant.nouveau.api.DocumentUpdateRequest;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

public class DocumentFactory {

    private FacetsConfig facetsConfig = new FacetsConfig();

    public Document build(final String docId, final DocumentUpdateRequest request) throws IOException {
        final Document result = new Document();

        // id
        result.add(new org.apache.lucene.document.StringField("_id", docId, Store.YES));
        result.add(new org.apache.lucene.document.SortedDocValuesField("_id", new BytesRef(docId)));

        for (IndexableField field : request.getFields()) {
            // Underscore-prefix is reserved.
            if (field.name().startsWith("_")) {
                continue;
            }
            result.add(field);
        }

        return facetsConfig.build(result);
    }

}
