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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

public class LuceneModule extends SimpleModule {

    public LuceneModule() {
        super("lucene", new Version(0, 0, 1, null));
        addSerializer(BytesRef.class, new BytesRefSerializer());
        setMixInAnnotation(FieldType.class, FieldTypeMixin.class);
        setMixInAnnotation(IndexableFieldType.class, FieldTypeMixin.class);
        setMixInAnnotation(Field.class, FieldMixin.class);
        setMixInAnnotation(IndexableField.class, FieldMixin.class);

        // Serializers

        // Sugar first
        // result.addSerializer(DoublePoint.class, new DoublePointSerializer());
        // result.addSerializer(IntPoint.class, new IntPointSerializer());
        // result.addSerializer(StringField.class, new StringFieldSerializer());
        // result.addSerializer(TextField.class, new TextFieldSerializer());
        // result.addSerializer(StoredField.class, new StoredFieldSerializer());

        // // Generic
        // result.addSerializer(IndexableField.class, new IndexableFieldSerializer());
        // result.addSerializer(IndexableFieldType.class, new IndexableFieldTypeSerializer());

        // //result.addDeserializer(IndexableField.class, new IndexableFieldDeserializer());
    }

}
