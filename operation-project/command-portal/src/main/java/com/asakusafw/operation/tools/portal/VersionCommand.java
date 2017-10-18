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
package com.asakusafw.operation.tools.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asakusafw.info.cli.list.ListBatchCommand;
import com.asakusafw.utils.jcommander.CommandConfigurationException;
import com.asakusafw.utils.jcommander.CommandException;
import com.asakusafw.utils.jcommander.common.HelpParameter;
import com.asakusafw.utils.jcommander.common.OutputParameter;
import com.asakusafw.utils.jcommander.common.VerboseParameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

/**
 * A command for printing list of batch.
 * @since 0.10.0
 */
@Parameters(
        commandNames = "version",
        commandDescription = "Displays Asakusa Framework version."
)
public class VersionCommand implements Runnable {

    static final Logger LOG = LoggerFactory.getLogger(ListBatchCommand.class);

    static final String ENV_ASAKUSA_HOME = "ASAKUSA_HOME";

    static final String PATH_VERSION = "VERSION";

    static final String KEY_VERSION = "asakusafw.version";

    static final String[] VERBOSE_SYSTEM_PROPERTIES = {
            "java.version",
            "java.vendor",
    };

    @ParametersDelegate
    final HelpParameter helpParameter = new HelpParameter();

    @ParametersDelegate
    final VerboseParameter verboseParameter = new VerboseParameter();

    @ParametersDelegate
    final OutputParameter outputParameter = new OutputParameter();

    @Override
    public void run() {
        LOG.debug("starting {}", getClass().getSimpleName());
        Path home = Optional.ofNullable(System.getenv(ENV_ASAKUSA_HOME))
                .map(Paths::get)
                .orElseThrow(() -> new CommandConfigurationException(MessageFormat.format(
                        "environment variable \"{0}\" must be defined",
                        ENV_ASAKUSA_HOME)));
        if (Files.isDirectory(home) == false) {
            throw new CommandConfigurationException(MessageFormat.format(
                    "framework installation is not found: {0}",
                    home));
        }
        Path file = home.resolve(PATH_VERSION);
        if (Files.isRegularFile(file) == false) {
            throw new CommandConfigurationException(MessageFormat.format(
                    "framework installation may be broken (missing \"{1}\"): {0}",
                    home,
                    PATH_VERSION));
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new CommandException(MessageFormat.format(
                    "error occurred while reading version info: {0}",
                    file), e);
        }
        String version = lines.stream()
                .map(String::trim)
                .map(it -> it.split("[=:]", 2))
                .filter(it -> it.length == 2)
                .filter(it -> it[0].equals(KEY_VERSION))
                .map(it -> it[1].trim())
                .findAny()
                .orElseThrow(() -> new CommandConfigurationException(MessageFormat.format(
                        "framework installation may be broken (missing \"{1}\"): {0}",
                        home,
                        KEY_VERSION)));

        try (PrintWriter writer = outputParameter.open()) {
            writer.println(version);
            if (verboseParameter.isRequired()) {
                writer.printf("ASAKUSA_HOME=%s%n", home);
                for (String k : VERBOSE_SYSTEM_PROPERTIES) {
                    writer.printf("%s=%s%n", k, System.getProperty(k, "N/A"));
                }
            }
        }
    }
}