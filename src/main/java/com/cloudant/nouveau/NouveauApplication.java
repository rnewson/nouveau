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

import com.cloudant.nouveau.core.AnalyzerFactory;
import com.cloudant.nouveau.core.DocumentFactory;
import com.cloudant.nouveau.core.FileAlreadyExistsExceptionMapper;
import com.cloudant.nouveau.core.FileNotFoundExceptionMapper;
import com.cloudant.nouveau.core.IndexManager;
import com.cloudant.nouveau.core.UpdatesOutOfOrderExceptionMapper;
import com.cloudant.nouveau.health.AnalyzeHealthCheck;
import com.cloudant.nouveau.health.IndexManagerHealthCheck;
import com.cloudant.nouveau.resources.AnalyzeResource;
import com.cloudant.nouveau.resources.IndexResource;
import com.cloudant.nouveau.resources.SearchResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class NouveauApplication extends Application<NouveauApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new NouveauApplication().run(args);
    }

    @Override
    public String getName() {
        return "Nouveau";
    }

    @Override
    public void run(NouveauApplicationConfiguration configuration, Environment environment) throws Exception {
        final DocumentFactory documentFactory = new DocumentFactory();
        final AnalyzerFactory analyzerFactory = new AnalyzerFactory();

        final IndexManager indexManager = new IndexManager();
        indexManager.setRootDir(configuration.getRootDir());
        indexManager.setMaxIndexesOpen(configuration.getMaxIndexesOpen());
        indexManager.setCommitIntervalSeconds(configuration.getCommitIntervalSeconds());
        indexManager.setIdleSeconds(configuration.getIdleSeconds());
        indexManager.setAnalyzerFactory(analyzerFactory);
        indexManager.setObjectMapper(environment.getObjectMapper());
        environment.lifecycle().manage(indexManager);

        environment.jersey().register(new FileNotFoundExceptionMapper());
        environment.jersey().register(new FileAlreadyExistsExceptionMapper());
        environment.jersey().register(new UpdatesOutOfOrderExceptionMapper());

        final AnalyzeResource analyzeResource = new AnalyzeResource(analyzerFactory);
        environment.jersey().register(analyzeResource);
        environment.jersey().register(new IndexResource(indexManager, documentFactory));
        environment.jersey().register(new SearchResource(indexManager));

        // health checks
        environment.healthChecks().register("analyzeResource", new AnalyzeHealthCheck(analyzeResource));
        environment.healthChecks().register("indexManager", new IndexManagerHealthCheck(indexManager));
    }

}
