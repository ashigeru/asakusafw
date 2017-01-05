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
package com.asakusafw.utils.java.model.syntax;

/**
 * An interface which represents annotation name and its value pair.
 * <ul>
 *   <li> Specified In: <ul>
 *     <li> {@code [JLS3:9.7] Annotations (ElementValuePair)} </li>
 *   </ul> </li>
 * </ul>
 */
public interface AnnotationElement
        extends Invocation {

    /**
     * Returns the annotation element name.
     * @return the annotation element name
     */
    SimpleName getName();

    /**
     * Returns the element value.
     * @return the element value
     */
    Expression getExpression();
}
