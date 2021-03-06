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
package com.asakusafw.operator.builtin;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import javax.lang.model.element.ExecutableElement;

import org.junit.Test;

import com.asakusafw.operator.description.Descriptions;
import com.asakusafw.operator.model.OperatorDescription;
import com.asakusafw.operator.model.OperatorDescription.Node;
import com.asakusafw.operator.model.OperatorDescription.Reference;
import com.asakusafw.operator.model.OperatorElement;
import com.asakusafw.vocabulary.operator.MasterJoin;

/**
 * Test for {@link MasterJoinOperatorDriver}.
 */
public class MasterJoinOperatorDriverTest extends OperatorDriverTestRoot {

    /**
     * Creates a new instance.
     */
    public MasterJoinOperatorDriverTest() {
        super(new MasterJoinOperatorDriver());
    }

    /**
     * annotation.
     */
    @Test
    public void annotationTypeName() {
        assertThat(driver.getAnnotationTypeName(), is(Descriptions.classOf(MasterJoin.class)));
    }

    /**
     * simple case.
     */
    @Test
    public void simple() {
        addDataModel("JModel");
        addDataModel("LModel");
        addDataModel("RModel");
        compile(new Action("com.example.Simple") {
            @Override
            protected void perform(OperatorElement target) {
                OperatorDescription description = target.getDescription();
                assertThat(description.getInputs().size(), is(2));
                assertThat(description.getOutputs().size(), is(2));
                assertThat(description.getArguments().size(), is(0));

                Node left = description.getInputs().get(0);
                assertThat(left.getName(), is("left"));
                assertThat(left.getType(), is(sameType("com.example.LModel")));
                assertThat(left.getReference(), is((Reference) Reference.parameter(0)));

                Node right = description.getInputs().get(1);
                assertThat(right.getName(), is("right"));
                assertThat(right.getType(), is(sameType("com.example.RModel")));
                assertThat(right.getReference(), is((Reference) Reference.parameter(1)));

                Map<String, Node> outputs = toMap(description.getOutputs());

                Node out = outputs.get(defaultName(MasterJoin.class, "joinedPort"));
                assertThat(out, is(notNullValue()));
                assertThat(out.getType(), is(sameType("com.example.JModel")));
                assertThat(out.getReference(), is(Reference.returns()));

                Node orig = outputs.get(defaultName(MasterJoin.class, "missedPort"));
                assertThat(orig, is(notNullValue()));
                assertThat(orig.getType(), is(sameType("com.example.RModel")));
            }
        });
    }

    /**
     * w/ selection.
     */
    @Test
    public void with_selection() {
        addDataModel("JModel");
        addDataModel("LModel");
        addDataModel("RModel");
        compile(new Action("com.example.WithSelection") {
            @Override
            protected void perform(OperatorElement target) {
                OperatorDescription description = target.getDescription();
                ExecutableElement support = description.getSupport();
                assertThat(support, is(notNullValue()));
                assertThat(support.getSimpleName().toString(), is("selector"));
            }
        });
    }

    /**
     * w/ extra parameter.
     */
    @Test
    public void with_extra_parameter() {
        addDataModel("JModel");
        addDataModel("LModel");
        addDataModel("RModel");
        compile(new Action("com.example.WithExtraParameter") {
            @Override
            protected void perform(OperatorElement target) {
                OperatorDescription description = target.getDescription();
                assertThat(description.getInputs().size(), is(2));
                assertThat(description.getOutputs().size(), is(2));
                assertThat(description.getArguments().size(), is(1));

                ExecutableElement support = description.getSupport();
                assertThat(support, is(notNullValue()));
                assertThat(support.getSimpleName().toString(), is("selector"));
            }
        });
    }

    /**
     * w/ view.
     */
    @Test
    public void with_view() {
        addDataModel("JModel");
        addDataModel("LModel");
        addDataModel("RModel");
        compile(new Action("com.example.WithView") {
            @Override
            protected void perform(OperatorElement target) {
                OperatorDescription description = target.getDescription();
                assertThat(description.getInputs().size(), is(3));
                assertThat(description.getOutputs().size(), is(2));
                assertThat(description.getArguments().size(), is(0));

                ExecutableElement support = description.getSupport();
                assertThat(support, is(notNullValue()));
                assertThat(support.getSimpleName().toString(), is("selector"));

                assertThat(description.getInputs().get(0).getType(), isType("LModel"));
                assertThat(description.getInputs().get(1).getType(), isType("RModel"));
                assertThat(description.getInputs().get(2).getType(), isType("ViewSide"));

                assertThat(description.getOutputs().get(0).getType(), isType("JModel"));
                assertThat(description.getOutputs().get(1).getType(), isType("RModel"));
            }
        });
    }

    /**
     * violates extra parameters should be with selector.
     */
    @Test
    public void violate_extra_parameter_with_selection() {
        addDataModel("JModel");
        addDataModel("LModel");
        addDataModel("RModel");
        violate("com.example.ViolateExtraParameterWithoutSelection");
    }
}
