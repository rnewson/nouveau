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

package com.cloudant.nouveau.core.ser;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.lucene.facet.range.DoubleRange;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.FieldDoc;

public class LuceneModule extends SimpleModule {

    public LuceneModule() {
        super("lucene", Version.unknownVersion());

        // IndexableField
        addSerializer(IndexableField.class, new IndexableFieldSerializer());
        addDeserializer(IndexableField.class, new IndexableFieldDeserializer());

        // DoubleRange
        addSerializer(DoubleRange.class, new DoubleRangeSerializer());
        addDeserializer(DoubleRange.class, new DoubleRangeDeserializer());

        // FieldDoc
        addSerializer(FieldDoc.class, new FieldDocSerializer());
        addDeserializer(FieldDoc.class, new FieldDocDeserializer());

        // BytesRef - disabled until I'm sure I need it.
        // addSerializer(BytesRef.class, new BytesRefSerializer());
        // addDeserializer(BytesRef.class, new BytesRefDeserializer());
    }

}
