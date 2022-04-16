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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;

public class StandardQueryParser implements QueryParser {

    private org.apache.lucene.queryparser.flexible.standard.StandardQueryParser adaptee;
    private String defaultField;

    public StandardQueryParser(final String defaultField, final Analyzer analyzer) {
        this.adaptee = new org.apache.lucene.queryparser.flexible.standard.StandardQueryParser(analyzer);
        this.defaultField = defaultField;
    }

    public Query parse(String query) throws QueryParserException {
        try {
            return adaptee.parse(query, defaultField);
        } catch (QueryNodeException e) {
            throw new QueryParserException(e);
        }
    }

}
