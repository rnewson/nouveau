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
import java.util.List;
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
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/index/{name}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResource.class);
    private static final Pattern SORT_FIELD_RE = Pattern.compile("^([-+])?([\\.\\w]+)(?:<(\\w+)>)?$");
    private final IndexManager indexManager;

    public SearchResource(final IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @POST
    @Timed
    @Path("/search")
    public SearchResults searchIndex(@PathParam("name") String name, @NotNull @Valid SearchRequest searchRequest) throws IOException, QueryParserException {
        final Index index = indexManager.acquire(name);
        try {
            final Query query = index.getQueryParser().parse(searchRequest.getQuery());
            final SearcherManager searcherManager = index.getSearcherManager();
            searcherManager.maybeRefreshBlocking();
            final IndexSearcher searcher = searcherManager.acquire();
            try {
                final TopDocs topDocs;
                if (searchRequest.hasSort()) {
                    final Sort sort = convertSort(searchRequest.getSort());
                    topDocs = searcher.search(query, searchRequest.getLimit(), sort);
                } else {
                    topDocs = searcher.search(query, searchRequest.getLimit());
                }
                return toSearchResults(searcher, topDocs);
            } catch (IllegalStateException e) {
                throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
            } finally {
                searcherManager.release(searcher);
            }
        } finally {
            indexManager.release(index);
        }
    }

    private SearchResults toSearchResults(final IndexSearcher searcher, final TopDocs topDocs) throws IOException {
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
        return new SearchResults(topDocs.totalHits.value, hits);
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
