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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.cloudant.nouveau.api.Sortable;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

// TODO use-lucene-classes - teach jackson to deserialize these directly.

public class LuceneUtils {

    private static final Pattern SORT_FIELD_RE = Pattern.compile("^([-+])?([\\.\\w]+)(?:<(\\w+)>)?$");

    // Ensure _id is final sort field so we can paginate.
    public static Sort toSort(final Sortable sortable, final Sort defaultSort) {
        if (!sortable.hasSort()) {
            return defaultSort;
        }
        final List<String> sort = new ArrayList<String>(sortable.getSort());
        if (!"_id<string>".equals(sort.get(sort.size() - 1))) {
            sort.add("_id<string>");
        }
        return convertSort(sort);
    }

    private static Sort convertSort(final List<String> sort) {
        final SortField[] fields = new SortField[sort.size()];
        for (int i = 0; i < sort.size(); i++) {
            fields[i] = convertSortField(sort.get(i));
        }
        return new Sort(fields);
    }

    private static SortField convertSortField(final String sortString) {
        final Matcher m = SORT_FIELD_RE.matcher(sortString);
        if (!m.matches()) {
            throw new WebApplicationException(sortString + " is not a valid sort parameter", Status.BAD_REQUEST);
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
