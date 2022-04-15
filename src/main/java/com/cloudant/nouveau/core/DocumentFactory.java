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
import com.cloudant.nouveau.api.DoubleField;
import com.cloudant.nouveau.api.StringField;
import com.cloudant.nouveau.api.TextField;
import com.cloudant.nouveau.api.Field;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.util.BytesRef;

public class DocumentFactory {

    private FacetsConfig facetsConfig = new FacetsConfig();

    public Document build(final String docId, final DocumentUpdateRequest request) throws IOException {
        final Document result = new Document();

        // id
        result.add(new org.apache.lucene.document.StringField("_id", docId, Store.YES));
        result.add(new org.apache.lucene.document.SortedDocValuesField("_id", new BytesRef(docId)));

        for (Field field : request.getFields()) {
            // Underscore-prefix is reserved.
            if (field.getName().startsWith("_")) {
                continue;
            }
            if (field instanceof DoubleField) {
                addTo(result, (DoubleField)field);
            }
            if (field instanceof StringField) {
                addTo(result, (StringField)field);
            }
            if (field instanceof TextField) {
                addTo(result, (TextField)field);
            }
        }

        return facetsConfig.build(result);
    }

    private void addTo(final Document doc, final DoubleField field) {
        doc.add(new org.apache.lucene.document.DoublePoint(field.getName(), field.getValue()));
        if (field.isStored()) {
            doc.add(new org.apache.lucene.document.StoredField(field.getName(), field.getValue()));
        }
        if (field.isFacet()) {
            doc.add(new org.apache.lucene.document.DoubleDocValuesField(field.getName(), field.getValue()));
        }
    }

    public void addTo(final Document doc, final StringField field) throws IOException {
        doc.add(new org.apache.lucene.document.StringField(field.getName(), field.getValue(),
                                                           field.isStored() ? Store.YES : Store.NO));
        if (field.isFacet()) {
            doc.add(new org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField(field.getName(), field.getValue()));
        }
    }

    public void addTo(final Document doc, final TextField field) {
        doc.add(new org.apache.lucene.document.TextField(field.getName(), field.getValue(),
                                                         field.isStored() ? Store.YES : Store.NO));
        doc.add(new org.apache.lucene.document.SortedDocValuesField(field.getName(), new BytesRef(field.getValue())));
    }

}
