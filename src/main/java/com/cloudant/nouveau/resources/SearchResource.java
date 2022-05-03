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

package com.cloudant.nouveau.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.cloudant.nouveau.api.SearchHit;
import com.cloudant.nouveau.api.SearchRequest;
import com.cloudant.nouveau.api.SearchResults;
import com.cloudant.nouveau.core.IndexManager;
import com.cloudant.nouveau.core.IndexManager.Index;
import com.cloudant.nouveau.core.QueryParserException;
import com.codahale.metrics.annotation.Timed;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsCollectorManager;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.StringDocValuesReaderState;
import org.apache.lucene.facet.StringValueFacetCounts;
import org.apache.lucene.facet.range.DoubleRange;
import org.apache.lucene.facet.range.DoubleRangeFacetCounts;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollectorManager;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/index/{name}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResource.class);
    private static final DoubleRange[] EMPTY_DOUBLE_RANGE_ARRAY = new DoubleRange[0];
    private static final Pattern SORT_FIELD_RE = Pattern.compile("^([-+])?([\\.\\w]+)(?:<(\\w+)>)?$");
    private final IndexManager indexManager;

    public SearchResource(final IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @POST
    @Timed
    @Path("/search")
    public SearchResults searchIndex(@PathParam("name") String name, @NotNull @Valid SearchRequest searchRequest)
            throws IOException, QueryParserException {
        final Index index = indexManager.acquire(name);
        try {
            final Query query = index.getQueryParser().parse(searchRequest.getQuery());

            // Construct CollectorManagers.
            final MultiCollectorManager cm;
            final CollectorManager<?, ? extends TopDocs> hits = hitCollector(searchRequest);

            final SearcherManager searcherManager = index.getSearcherManager();
            searcherManager.maybeRefreshBlocking();

            final IndexSearcher searcher = searcherManager.acquire();
            try {
                if (searchRequest.hasCounts() || searchRequest.hasRanges()) {
                    cm = new MultiCollectorManager(hits, new FacetsCollectorManager());
                } else {
                    cm = new MultiCollectorManager(hits);
                }
                final Object[] reduces = searcher.search(query, cm);
                return toSearchResults(searchRequest, searcher, reduces);
            } catch (IllegalStateException e) {
                throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
            } finally {
                searcherManager.release(searcher);
            }
        } finally {
            indexManager.release(index);
        }
    }

    private CollectorManager<?, ? extends TopDocs> hitCollector(final SearchRequest searchRequest) {
        if (searchRequest.hasSort()) {
            return TopFieldCollector.createSharedManager(
                    convertSort(searchRequest.getSort()),
                    searchRequest.getLimit(),
                    null,
                    1000);
        }

        return TopScoreDocCollector.createSharedManager(
                searchRequest.getLimit(),
                null,
                1000);
    }

    private SearchResults toSearchResults(final SearchRequest searchRequest, final IndexSearcher searcher, final Object[] reduces) throws IOException {
        final SearchResults result = new SearchResults();
        collectHits(searcher, (TopDocs)reduces[0], result);
        if (reduces.length == 2) {
            collectFacets(searchRequest, searcher, (FacetsCollector)reduces[1], result);
        }
        return result;
    }

    private void collectHits(final IndexSearcher searcher, final TopDocs topDocs, final SearchResults searchResults) throws IOException {
        final List<SearchHit> hits = new ArrayList<SearchHit>(topDocs.scoreDocs.length);

        for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
            final Document doc = searcher.doc(scoreDoc.doc);

            final List<Object> order;
            if (scoreDoc instanceof FieldDoc) {
                final FieldDoc fieldDoc = (FieldDoc) scoreDoc;
                order = new ArrayList<Object>(fieldDoc.fields.length + 1);
                order.addAll(Arrays.asList(fieldDoc.fields));
                order.add(doc.get("_id"));
            } else {
                order = Arrays.asList(scoreDoc.score, doc.get("_id"));
            }

            final List<IndexableField> fields = new ArrayList<IndexableField>(doc.getFields());
            for (IndexableField field : doc.getFields()) {
                if (field.name().equals("_id")) {
                    fields.remove(field);
                }
            }

            hits.add(new SearchHit(doc.get("_id"), order, fields));
        }

        searchResults.setTotalHits(topDocs.totalHits.value);
        searchResults.setHits(hits);
    }

    private void collectFacets(final SearchRequest searchRequest, final IndexSearcher searcher, final FacetsCollector fc, final SearchResults searchResults) throws IOException {
        if (searchRequest.hasCounts()) {
            final Map<String, Map<String, Number>> countsMap = new HashMap<String, Map<String, Number>>(searchRequest.getCounts().size());
            for (final String field : searchRequest.getCounts()) {
                final StringDocValuesReaderState state = new StringDocValuesReaderState(searcher.getIndexReader(), field);
                final StringValueFacetCounts counts = new StringValueFacetCounts(state, fc);
                countsMap.put(field, collectFacets(counts, 10, field));
            }
            searchResults.setCounts(countsMap);
        }

        if (searchRequest.hasRanges()) {
            final Map<String, Map<String, Number>> rangesMap = new HashMap<String, Map<String, Number>>(searchRequest.getRanges().size());
            for (final Entry<String, List<DoubleRange>> entry : searchRequest.getRanges().entrySet()) {
                final DoubleRangeFacetCounts counts = new DoubleRangeFacetCounts(entry.getKey(), fc, entry.getValue().toArray(EMPTY_DOUBLE_RANGE_ARRAY));
                rangesMap.put(entry.getKey(), collectFacets(counts, 10, entry.getKey()));
            }
            searchResults.setRanges(rangesMap);
        }
    }

    private Map<String, Number> collectFacets(final Facets facets, final int topN, final String dim) throws IOException {
        final FacetResult topChildren = facets.getTopChildren(topN, dim);
        final Map<String, Number> result = new HashMap<String, Number>(topChildren.childCount);
        for (final LabelAndValue lv : topChildren.labelValues) {
            result.put(lv.label, lv.value);
        }
        return result;
    }

    private Sort convertSort(final List<String> sort) {
        final SortField[] fields = new SortField[sort.size()];
        for (int i = 0; i < sort.size(); i++) {
            fields[0] = convertSortField(sort.get(i));
        }
        return new Sort(fields);
    }

    private SortField convertSortField(final String sortString) {
        final Matcher m = SORT_FIELD_RE.matcher(sortString);
        if (!m.matches()) {
            throw new WebApplicationException(
                    sortString + " is not a valid sort parameter", Status.BAD_REQUEST);
        }
        final boolean reverse = "-".equals(m.group(1));
        final SortField.Type type;
        switch (m.group(3)) {
            case "string":
                type = SortField.Type.STRING;
                break;
            case "number":
            default:
                type = SortField.Type.DOUBLE;
                break;
        }
        return new SortField(m.group(2), type, reverse);
    }

}
