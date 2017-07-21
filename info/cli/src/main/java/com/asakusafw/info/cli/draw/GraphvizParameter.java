/**
 * Copyright 2011-2017 Asakusa Framework Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asakusafw.info.cli.draw;

import java.util.LinkedHashMap;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;

/**
 * Provides graphviz options.
 * @since 0.10.0
 */
public class GraphvizParameter {

    @DynamicParameter(
            names = { "-G", "--graph-option" },
            description = "Puts a graph option."
    )
    Map<String, String> graphOptions = new LinkedHashMap<>();

    @DynamicParameter(
            names = { "-N", "--node-option" },
            description = "Puts a node option."
            )
    Map<String, String> nodeOptions = new LinkedHashMap<>();

    @DynamicParameter(
            names = { "-E", "--edge-option" },
            description = "Puts a edge option."
            )
    Map<String, String> edgeOptions = new LinkedHashMap<>();

    /**
     * Returns the graph options.
     * @return the graph options
     */
    public Map<String, ?> getOptions() {
        Map<String, String> graphs = new LinkedHashMap<>(graphOptions);
        graphs.put("compound", String.valueOf(true));

        Map<String, String> nodes = new LinkedHashMap<>(nodeOptions);
        Map<String, String> edges = new LinkedHashMap<>(edgeOptions);

        Map<String, Object> results = new LinkedHashMap<>();
        results.put("graph", graphs);
        results.put("node", nodes);
        results.put("edge", edges);
        return results;
    }
}
