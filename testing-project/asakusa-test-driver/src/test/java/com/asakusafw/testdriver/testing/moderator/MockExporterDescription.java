/**
 * Copyright 2011-2019 Asakusa Framework Team.
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
package com.asakusafw.testdriver.testing.moderator;

import com.asakusafw.vocabulary.external.ExporterDescription;

/**
 * Mock implementation of {@link ExporterDescription}.
 * @since 0.9.0
 */
public abstract class MockExporterDescription implements ExporterDescription {

    /**
     * Returns the output path glob.
     * @return the output path glob
     */
    public abstract String getGlob();
}
