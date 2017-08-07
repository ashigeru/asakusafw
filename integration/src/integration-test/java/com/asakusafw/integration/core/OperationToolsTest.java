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
package com.asakusafw.integration.core;

import static com.asakusafw.integration.core.Util.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.asakusafw.integration.AsakusaConfigurator;
import com.asakusafw.integration.AsakusaProject;
import com.asakusafw.integration.AsakusaProjectProvider;
import com.asakusafw.utils.gradle.Bundle;
import com.asakusafw.utils.gradle.ContentsConfigurator;

/**
 * Test for {@code $ASAKUSA_HOME/operation-tools}.
 */
@RunWith(Parameterized.class)
public class OperationToolsTest {

    /**
     * Return the test parameters.
     * @return the test parameters
     */
    @Parameters(name = "use-hadoop:{0}")
    public static Object[][] getTestParameters() {
        return new Object[][] {
            { false },
            { true },
        };
    }

    /**
     * project provider.
     */
    @Rule
    public final AsakusaProjectProvider provider = new AsakusaProjectProvider()
            .withProject(ContentsConfigurator.copy(data("organizer-simple")));

    /**
     * Creates a new instance.
     * @param useHadoop whether or not the test uses hadoop command
     */
    public OperationToolsTest(boolean useHadoop) {
        if (useHadoop) {
            provider.withProject(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.SKIP_IF_UNDEFINED));
        } else {
            provider.withProject(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.UNSET_ALWAYS));
        }
    }

    /**
     * {@code hadoop-fs-clean.sh -help}.
     */
    @Test
    public void hadoop_fs_clean_help() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle framework = project.getFramework();
        assertThat(
                framework.launch("tools/bin/hadoop-fs-clean.sh",
                        "-help"),
                is(0));
    }

    /**
     * {@code hadoop-fs-clean.sh}.
     */
    @Test
    public void hadoop_fs_clean_delete() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        assertThat(
                framework.launch("tools/bin/hadoop-fs-clean.sh",
                        "-k", "0",
                        contents.get("var/file.txt").toUri().toString()),
                is(0));

        assertThat(contents.find("var/file.txt"), is(Optional.empty()));
    }

    /**
     * {@code hadoop-fs-clean.sh -dry-run}.
     */
    @Test
    public void hadoop_fs_clean_dryrun() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        assertThat(
                framework.launch("tools/bin/hadoop-fs-clean.sh",
                        "-k", "0",
                        "-dry-run",
                        contents.get("var/file.txt").toUri().toString()),
                is(0));

        assertThat(contents.find("var/file.txt"), is(not(Optional.empty())));
    }

    /**
     * {@code hadoop-fs-clean.sh} don't delete directories.
     */
    @Test
    public void hadoop_fs_clean_skip_dir() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        assertThat(
                framework.launch("tools/bin/hadoop-fs-clean.sh",
                        "-k", "0",
                        contents.get("var").toUri().toString()),
                is(0));

        assertThat(contents.find("var/file.txt"), is(not(Optional.empty())));
    }

    /**
     * {@code hadoop-fs-clean.sh -r} can delete directories.
     */
    @Test
    public void hadoop_fs_clean_recursive() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        assertThat(
                framework.launch("tools/bin/hadoop-fs-clean.sh",
                        "-k", "0",
                        "-recursive",
                        contents.get("var").toUri().toString()),
                is(0));

        assertThat(contents.find("var"), is(Optional.empty()));
    }

    /**
     * {@code info.sh}.
     */
    @Test
    public void info() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        framework.withLaunch("tools/bin/info.sh");
    }

    /**
     * {@code info.sh list batch}.
     */
    @Test
    public void info_list_batch() {
        AsakusaProject project = provider.newInstance("tls");
        project.gradle("installAsakusafw");

        Bundle contents = project.getContents();
        contents.put("var/file.txt");

        Bundle framework = project.getFramework();
        framework.withLaunch("tools/bin/info.sh", "list", "batch", "-v");
    }
}
