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
package com.asakusafw.info.cli.list;

import com.asakusafw.utils.jcommander.common.GroupUsageCommand;
import com.beust.jcommander.Parameters;

/**
 * A group command for WindGate list.
 * @since 0.10.0
 */
@Parameters(
        commandNames = "windgate",
        commandDescription = "Displays WindGate information."
)
public class ListWindGateGroup extends GroupUsageCommand {
    // no special members
}