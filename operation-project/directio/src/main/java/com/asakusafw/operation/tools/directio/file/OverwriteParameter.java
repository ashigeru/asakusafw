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
package com.asakusafw.operation.tools.directio.file;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Handles parameters whether or not overwrite target resources.
 * @since 0.10.0
 */
@Parameters(resourceBundle = "com.asakusafw.operation.tools.directio.jcommander")
public class OverwriteParameter {

    @Parameter(
            names = { "-w", "--overwrite" },
            descriptionKey = "parameter.overwrite",
            required = false)
    boolean overwrite = false;

    /**
     * Returns whether or not the operation may overwrite destination files.
     * @return {@code true} to overwrite, otherwise {@code false}
     */
    public boolean isEnabled() {
        return overwrite;
    }
}
