/**
 * Copyright 2011-2012 Asakusa Framework Team.
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
package com.asakusafw.runtime.stage;

import static com.asakusafw.runtime.stage.StageConstants.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import com.asakusafw.runtime.util.VariableTable;
import com.asakusafw.runtime.util.VariableTable.RedefineStrategy;

/**
 * Stage client for cleanup phase.
 * @since 0.2.6
 */
public abstract class AbstractCleanupStageClient extends BaseStageClient {

    /**
     * Fully qualified class name of implementation of this class.
     */
    public static final String IMPLEMENTATION = "com.asakusafw.runtime.stage.CleanupStageClient";

    /**
     * The method name of {@link #getCleanupPath()}.
     */
    public static final String METHOD_CLEANUP_PATH = "getCleanupPath";

    static final Log LOG = LogFactory.getLog(AbstractCleanupStageClient.class);

    /**
     * Returns the cleanup target path.
     * @return the cleanup target path
     */
    protected abstract String getCleanupPath();

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        Path path = getPath(conf);
        FileSystem fileSystem = FileSystem.get(path.toUri(), conf);
        try {
            LOG.info(MessageFormat.format(
                    "Searching for cleanup target: batchId={0}, flowId={1}, executionId={2}, path={3}",
                    getBatchId(),
                    getFlowId(),
                    getExecutionId(),
                    path));
            long start = System.currentTimeMillis();
            FileStatus stat = fileSystem.getFileStatus(path);
            if (stat == null) {
                throw new FileNotFoundException(path.toString());
            }
            LOG.info(MessageFormat.format(
                    "Start deleting cleanup target: batchId={0}, flowId={1}, executionId={2}, path={3}",
                    getBatchId(),
                    getFlowId(),
                    getExecutionId(),
                    path));
            if (fileSystem.delete(path, true) == false) {
                throw new IOException("FileSystem.delete() returned false");
            }
            long end = System.currentTimeMillis();
            LOG.info(MessageFormat.format(
                    "Finish deleting cleanup target: batchId={0}, flowId={1}, executionId={2}, path={3}",
                    getBatchId(),
                    getFlowId(),
                    getExecutionId(),
                    path,
                    end - start));
            return 0;
        } catch (FileNotFoundException e) {
            LOG.warn(MessageFormat.format(
                    "Cleanup target is missing: batchId={0}, flowId={1}, executionId={2}, path={3}",
                    getBatchId(),
                    getFlowId(),
                    getExecutionId(),
                    path));
            return 0;
        } catch (IOException e) {
            LOG.warn(MessageFormat.format(
                    "Failed to delete cleanup target: batchId={0}, flowId={1}, executionId={2}, path={3}",
                    getBatchId(),
                    getFlowId(),
                    getExecutionId(),
                    path), e);
            return 1;
        } finally {
            FileSystem.closeAll();
        }
    }

    private Path getPath(Configuration conf) {
        VariableTable variables = getPathParser(conf);
        String barePath = getCleanupPath();
        String pathString = variables.parse(barePath, false);
        Path path = new Path(pathString);
        return path;
    }

    private VariableTable getPathParser(Configuration configuration) {
        assert configuration != null;
        VariableTable variables = new VariableTable(RedefineStrategy.IGNORE);
        variables.defineVariable(VAR_USER, getUser());
        variables.defineVariable(VAR_DEFINITION_ID, getDefinitionId());
        variables.defineVariable(VAR_STAGE_ID, getStageId());
        variables.defineVariable(VAR_BATCH_ID, getBatchId());
        variables.defineVariable(VAR_FLOW_ID, getFlowId());
        variables.defineVariable(VAR_EXECUTION_ID, getExecutionId());
        String arguments = configuration.get(PROP_ASAKUSA_BATCH_ARGS);
        if (arguments == null) {
            LOG.warn(MessageFormat.format(
                    "A mandatory property \"{0}\" does not defined",
                    PROP_ASAKUSA_BATCH_ARGS));
        } else {
            variables.defineVariables(arguments);
        }
        return variables;
    }
}
