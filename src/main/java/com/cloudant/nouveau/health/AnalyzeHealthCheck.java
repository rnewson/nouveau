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

package com.cloudant.nouveau.health;

import java.util.Arrays;
import java.util.List;

import com.cloudant.nouveau.api.AnalyzeRequest;
import com.cloudant.nouveau.api.AnalyzeResponse;
import com.cloudant.nouveau.resources.AnalyzeResource;
import com.codahale.metrics.health.HealthCheck;

public class AnalyzeHealthCheck extends HealthCheck {

    private AnalyzeResource analyzeResource;

    public AnalyzeHealthCheck(final AnalyzeResource analyzeResource) {
        this.analyzeResource = analyzeResource;
    }

    @Override
    protected Result check() throws Exception {
        final AnalyzeRequest request = new AnalyzeRequest("standard", "hello there");
        final AnalyzeResponse response = analyzeResource.analyzeText(request);
        final List<String> expected = Arrays.asList("hello", "there");
        final List<String> actual = response.getTokens();
        if (expected.equals(actual)) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Expected '{}' but got '{}'", expected, actual);
        }
    }

}
