/**
 * Copyright 2011-2016 Asakusa Framework Team.
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
package com.asakusafw.utils.java.internal.model.syntax;

import com.asakusafw.utils.java.model.syntax.Expression;
import com.asakusafw.utils.java.model.syntax.ModelKind;
import com.asakusafw.utils.java.model.syntax.ReturnStatement;
import com.asakusafw.utils.java.model.syntax.Visitor;

/**
 * An implementation of {@link ReturnStatement}.
 */
public final class ReturnStatementImpl extends ModelRoot implements ReturnStatement {

    private Expression expression;

    @Override
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Sets the return value.
     * @param expression the return value, or {@code null} if it is not specified
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Returns {@link ModelKind#RETURN_STATEMENT} which represents this element kind.
     * @return {@link ModelKind#RETURN_STATEMENT}
     */
    @Override
    public ModelKind getModelKind() {
        return ModelKind.RETURN_STATEMENT;
    }

    @Override
    public <R, C, E extends Throwable> R accept(Visitor<R, C, E> visitor, C context) throws E {
        Util.notNull(visitor, "visitor"); //$NON-NLS-1$
        return visitor.visitReturnStatement(this, context);
    }
}
