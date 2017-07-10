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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;

import com.asakusafw.integration.AsakusaProject;
import com.asakusafw.integration.AsakusaProjectProvider;
import com.asakusafw.utils.gradle.Bundle;
import com.asakusafw.utils.gradle.ContentsConfigurator;

/**
 * Test for {@code asakusafw-organizer}.
 */
public class AsakusaOrganizerTest {

    /**
     * project provider.
     */
    @Rule
    public final AsakusaProjectProvider provider = new AsakusaProjectProvider()
        .withProject(ContentsConfigurator.copy("src/integration-test/data/organizer-simple"));

    /**
     * help.
     */
    @Test
    public void help() {
        AsakusaProject project = provider.newInstance("simple");
        project.gradle("help");
    }

    /**
     * upgrade.
     */
    @Test
    public void upgrade() {
        AsakusaProject project = provider.newInstance("simple");
        project.gradle("asakusaUpgrade");
        Bundle contents = project.getContents();
        assertThat(contents.find("gradlew"), is(not(Optional.empty())));
        assertThat(contents.find("gradlew.bat"), is(not(Optional.empty())));
    }

    /**
     * install.
     */
    @Test
    public void install() {
        AsakusaProject project = provider.newInstance("simple");
        project.gradle("installAsakusafw");
        Bundle framework = project.getFramework();
        assertThat(framework.find("yaess/bin/yaess-batch.sh"), is(not(Optional.empty())));
    }

    /**
     * assemble.
     */
    @Test
    public void assemble() {
        AsakusaProject project = provider.newInstance("simple");
        project.gradle("assembleAsakusafw");
        Bundle contents = project.getContents();
        assertThat(contents.find("build/asakusafw-simple.tar.gz"), is(not(Optional.empty())));
    }
}
