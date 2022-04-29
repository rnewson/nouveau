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

package com.cloudant.nouveau;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.nouveau.api.DocumentUpdateRequest;
import com.cloudant.nouveau.api.DoubleField;
import com.cloudant.nouveau.api.IndexDefinition;
import com.cloudant.nouveau.api.SearchRequest;
import com.cloudant.nouveau.api.SearchResults;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {

    private static final String CONFIG = "test-nouveau.yaml";

    static final DropwizardAppExtension<NouveauApplicationConfiguration> APP = new DropwizardAppExtension<>(
            NouveauApplication.class, CONFIG,
            new ResourceConfigurationSourceProvider()
    );

    @Test
    public void indexTest() {
        final String url = "http://localhost:" + APP.getLocalPort();
        final String indexName = "foo";
        final IndexDefinition indexDefinition = new IndexDefinition("standard", null);

        // Clean up.
        Response response =
                APP.client().target(String.format("%s/index/%s", url, indexName))
                .request()
                .delete();

        // Create index.
        response =
                APP.client().target(String.format("%s/index/%s", url, indexName))
                .request()
                .put(Entity.entity(indexDefinition, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response).extracting(Response::getStatus)
        .isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        // Populate index
        for (int i = 0; i < 10; i++) {
            final DocumentUpdateRequest docUpdate = new DocumentUpdateRequest(i + 1,
                Collections.singletonList(new DoubleField("foo", i, true, true)));
            response = 
                APP.client().target(String.format("%s/index/%s/doc/doc%d", url, indexName, i))
                .request()
                .put(Entity.entity(docUpdate, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response).extracting(Response::getStatus)
            .isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        }

        // Search index
        final SearchRequest search = new SearchRequest("*:*", 10);
        response = 
                APP.client().target(String.format("%s/index/%s/search", url, indexName))
                .request()
                .post(Entity.entity(search, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response).extracting(Response::getStatus)
            .isEqualTo(Response.Status.OK.getStatusCode());

        final SearchResults results = response.readEntity(SearchResults.class);
        assertThat(results.getTotalHits()).isEqualTo(10);
    }

    @Test
    public void healthCheckShouldSucceed() {
        final Response healthCheckResponse =
                APP.client().target("http://localhost:" + APP.getAdminPort() + "/healthcheck")
                .request()
                .get();

        assertThat(healthCheckResponse)
                .extracting(Response::getStatus)
                .isEqualTo(Response.Status.OK.getStatusCode());
    }

}