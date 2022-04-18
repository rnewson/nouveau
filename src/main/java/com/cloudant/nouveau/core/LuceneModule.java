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

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.XYPointField;
import org.apache.lucene.index.IndexableField;

public class LuceneModule extends SimpleModule {

    public LuceneModule() {
        super("lucene", Version.unknownVersion());

        // Serializers
        addSerializer(DoublePoint.class, new DoublePointSerializer());
        addSerializer(IntPoint.class, new IntPointSerializer());
        addSerializer(FloatPoint.class, new FloatPointSerializer());
        addSerializer(LongPoint.class, new LongPointSerializer());
        addSerializer(StringField.class, new StringFieldSerializer());
        addSerializer(TextField.class, new TextFieldSerializer());
        addSerializer(StoredField.class, new StoredFieldSerializer());
        addSerializer(XYPointField.class, new XYPointFieldSerializer());

        // Deserializer
        addDeserializer(IndexableField.class, new IndexableFieldDeserializer());
    }

}
