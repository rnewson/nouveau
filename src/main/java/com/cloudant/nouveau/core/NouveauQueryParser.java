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
import org.apache.lucene.queryparser.flexible.core.QueryParserHelper;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.processors.NoChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryparser.flexible.core.processors.RemoveDeletedQueryNodesProcessor;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.ConfigurationKeys;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.processors.AllowLeadingWildcardProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.AnalyzerQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BooleanQuery2ModifierNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BooleanSingleChildOptimizationQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BoostQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.DefaultPhraseSlopQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.FuzzyQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.IntervalQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MatchAllDocsQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MultiFieldQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.MultiTermRewriteMethodProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.OpenRangeQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.PhraseSlopQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.PointQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.RegexpQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.RemoveEmptyNonLeafQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.TermRangeQueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.WildcardQueryNodeProcessor;
import org.apache.lucene.search.Query;

public class NouveauQueryParser extends QueryParserHelper implements QueryParser {

    private static class NouveauQueryNodeProcessorPipeline extends QueryNodeProcessorPipeline {

        public NouveauQueryNodeProcessorPipeline(QueryConfigHandler queryConfig) {
            super(queryConfig);

            add(new WildcardQueryNodeProcessor());
            add(new MultiFieldQueryNodeProcessor());
            add(new FuzzyQueryNodeProcessor());
            add(new RegexpQueryNodeProcessor());
            add(new MatchAllDocsQueryNodeProcessor());
            add(new OpenRangeQueryNodeProcessor());
            add(new PointQueryNodeProcessor());
            add(new NumericRangeQueryProcessor());
            add(new TermRangeQueryNodeProcessor());
            add(new AllowLeadingWildcardProcessor());
            add(new AnalyzerQueryNodeProcessor());
            add(new PhraseSlopQueryNodeProcessor());
            add(new BooleanQuery2ModifierNodeProcessor());
            add(new NoChildOptimizationQueryNodeProcessor());
            add(new RemoveDeletedQueryNodesProcessor());
            add(new RemoveEmptyNonLeafQueryNodeProcessor());
            add(new BooleanSingleChildOptimizationQueryNodeProcessor());
            add(new DefaultPhraseSlopQueryNodeProcessor());
            add(new BoostQueryNodeProcessor());
            add(new MultiTermRewriteMethodProcessor());
            add(new IntervalQueryNodeProcessor());
        }

    }

    private final String defaultField;

    public NouveauQueryParser(final String defaultField, final Analyzer analyzer) {
        super(
                new StandardQueryConfigHandler(),
                new StandardSyntaxParser(),
                new NouveauQueryNodeProcessorPipeline(null),
                new StandardQueryTreeBuilder());
        setEnablePositionIncrements(true);
        this.setAnalyzer(analyzer);
        this.defaultField = defaultField;
    }

    public void setAnalyzer(Analyzer analyzer) {
        getQueryConfigHandler().set(ConfigurationKeys.ANALYZER, analyzer);
    }

    public void setEnablePositionIncrements(boolean enabled) {
        getQueryConfigHandler().set(ConfigurationKeys.ENABLE_POSITION_INCREMENTS, enabled);
      }

    public Query parse(String query) throws QueryParserException {
        try {
            return (Query)parse(query, defaultField);
        } catch (QueryNodeException e) {
            throw new QueryParserException(e);
        }
    }

}
