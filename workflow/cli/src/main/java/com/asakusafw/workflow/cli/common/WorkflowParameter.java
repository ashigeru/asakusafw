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
package com.asakusafw.workflow.cli.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asakusafw.utils.jcommander.CommandConfigurationException;
import com.asakusafw.workflow.executor.ExecutionContext;
import com.asakusafw.workflow.executor.TaskExecutors;
import com.asakusafw.workflow.model.BatchInfo;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles parameters about workflow script.
 * @since 0.10.0
 */
public class WorkflowParameter {

    static final Logger LOG = LoggerFactory.getLogger(WorkflowParameter.class);

    /**
     * The workflow location.
     * It must be one of batch ID, batch application directory, or workflow information file.
     */
    @Parameter(
            description = "batch-ID",
            required = false
    )
    String workflow;

    /**
     * Returns workflow information.
     * @param context the current context
     * @return workflow information
     * @throws CommandConfigurationException if there is no available workflow file
     */
    public BatchInfo getBatchInfo(ExecutionContext context) {
        if (workflow == null) {
            throw new CommandConfigurationException(MessageFormat.format(
                    "target batch ID must be specified ({0})",
                    getAvailableApplicationsMessage(context)));
        }
        Optional<Path> path = resolvePath(context);
        if (path.isPresent()) {
            File file = path.get().toFile();
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(file, BatchInfo.class);
            } catch (IOException e) {
                throw new CommandConfigurationException(MessageFormat.format(
                        "error occurred while loading workflow definition: {0}",
                        file), e);
            }
        } else {
            throw new CommandConfigurationException(MessageFormat.format(
                    "batch application \"{0}\"is not found ({1})",
                    workflow,
                    getAvailableApplicationsMessage(context)));
        }
    }

    private static String getAvailableApplicationsMessage(ExecutionContext context) {
        List<String> apps = getAvailableApplications(context);
        if (apps.isEmpty()) {
            return "there are no available applications";
        } else {
            return MessageFormat.format("available applications are {0}", apps);
        }
    }

    private Optional<Path> resolvePath(ExecutionContext context) {
        Path path = Paths.get(workflow);
        if (Files.isDirectory(path)) {
            return Optional.of(path)
                    .map(it -> it.resolve(TaskExecutors.LOCATION_APPLICATION_WORKFLOW_DEFINITION))
                    .filter(Files::isRegularFile);
        } else if (Files.isRegularFile(path)) {
            return Optional.of(path)
                    .filter(Files::isRegularFile);
        } else if (path.getNameCount() == 1) {
            return TaskExecutors.findApplicationHome(context)
                    .map(it -> it.resolve(path.toString()))
                    .map(it -> it.resolve(TaskExecutors.LOCATION_APPLICATION_WORKFLOW_DEFINITION))
                    .filter(Files::isRegularFile);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a list of available batch application names on the current context.
     * @param context the current context
     * @return the available application names
     */
    public static List<String> getAvailableApplications(ExecutionContext context) {
        return TaskExecutors.findApplicationHome(context)
                .filter(Files::isDirectory)
                .map(it -> {
                    try {
                        return Files.list(it);
                    } catch (IOException e) {
                        throw new CommandConfigurationException(MessageFormat.format(
                                "exception occurred while traversing directory: {0}",
                                it), e);
                    }
                })
                .orElse(Stream.empty())
                .filter(it -> Files.exists(it.resolve(TaskExecutors.LOCATION_APPLICATION_WORKFLOW_DEFINITION)))
                .map(Path::getFileName)
                .filter(it -> it != null)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.toList());
    }
}
