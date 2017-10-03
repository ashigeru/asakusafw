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
package com.asakusafw.info.cli.generate;

import com.asakusafw.info.cli.generate.ddl.DdlGroup;
import com.asakusafw.info.cli.generate.dot.DotGroup;
import com.asakusafw.utils.jcommander.CommandBuilder;
import com.asakusafw.utils.jcommander.common.CommandProvider;
import com.asakusafw.utils.jcommander.common.GroupUsageCommand;
import com.beust.jcommander.Parameters;

/**
 * A group command for Direct I/O Hive tools.
 * @since 0.10.0
 */
@Parameters(
        commandNames = "generate",
        commandDescription = "Generates resources from DSL information."
)
public class GenerateGroup extends GroupUsageCommand implements CommandProvider {

    @Override
    public void accept(CommandBuilder<Runnable> builder) {
        builder.addGroup(new GenerateGroup(), group -> group
                .configure(new DotGroup())
                .configure(new DdlGroup()));
    }
}