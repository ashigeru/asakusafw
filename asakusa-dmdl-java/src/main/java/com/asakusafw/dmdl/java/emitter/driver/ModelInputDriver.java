/**
 * Copyright 2011 Asakusa Framework Team.
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
package com.asakusafw.dmdl.java.emitter.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.asakusafw.dmdl.java.emitter.EmitContext;
import com.asakusafw.dmdl.java.emitter.NameConstants;
import com.asakusafw.dmdl.java.spi.JavaDataModelDriver;
import com.asakusafw.dmdl.semantics.ModelDeclaration;
import com.asakusafw.dmdl.semantics.PropertyDeclaration;
import com.asakusafw.runtime.io.ModelInput;
import com.asakusafw.runtime.io.RecordParser;
import com.asakusafw.runtime.model.ModelInputLocation;
import com.ashigeru.lang.java.model.syntax.Annotation;
import com.ashigeru.lang.java.model.syntax.ClassDeclaration;
import com.ashigeru.lang.java.model.syntax.Expression;
import com.ashigeru.lang.java.model.syntax.FormalParameterDeclaration;
import com.ashigeru.lang.java.model.syntax.InfixOperator;
import com.ashigeru.lang.java.model.syntax.MethodDeclaration;
import com.ashigeru.lang.java.model.syntax.ModelFactory;
import com.ashigeru.lang.java.model.syntax.SimpleName;
import com.ashigeru.lang.java.model.syntax.Statement;
import com.ashigeru.lang.java.model.syntax.Type;
import com.ashigeru.lang.java.model.syntax.TypeBodyDeclaration;
import com.ashigeru.lang.java.model.syntax.TypeParameterDeclaration;
import com.ashigeru.lang.java.model.util.AttributeBuilder;
import com.ashigeru.lang.java.model.util.ExpressionBuilder;
import com.ashigeru.lang.java.model.util.JavadocBuilder;
import com.ashigeru.lang.java.model.util.Models;
import com.ashigeru.lang.java.model.util.TypeBuilder;

/**
 * Generates {@link ModelInput} for each data model.
 */
public class ModelInputDriver implements JavaDataModelDriver {

    @Override
    public List<Type> getInterfaces(EmitContext context, ModelDeclaration model) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodDeclaration> getMethods(EmitContext context, ModelDeclaration model) {
        return Collections.emptyList();
    }

    @Override
    public List<Annotation> getTypeAnnotations(EmitContext context, ModelDeclaration model) throws IOException {
        Type type = generate(context, model);
        ModelFactory f = context.getModelFactory();
        return new AttributeBuilder(f)
            .annotation(context.resolve(ModelInputLocation.class),
                    f.newClassLiteral(context.resolve(type)))
            .toAnnotations();
    }

    private Type generate(EmitContext context, ModelDeclaration model) throws IOException {
        EmitContext next = new EmitContext(
                context.getSemantics(),
                context.getConfiguration(),
                model,
                NameConstants.CATEGORY_IO,
                "{0}Input");
        Generator.emit(next, model);
        return context.resolve(next.getQualifiedTypeName());
    }

    @Override
    public List<Annotation> getMemberAnnotations(EmitContext context, PropertyDeclaration property) {
        return Collections.emptyList();
    }

    private static class Generator {

        private final EmitContext context;

        private final ModelDeclaration model;

        private final ModelFactory f;

        private Generator(EmitContext context, ModelDeclaration model) {
            assert context != null;
            assert model != null;
            this.context = context;
            this.model = model;
            this.f = context.getModelFactory();
        }

        static void emit(EmitContext context, ModelDeclaration model) throws IOException {
            assert context != null;
            assert model != null;
            Generator emitter = new Generator(context, model);
            emitter.emit();
        }

        private void emit() throws IOException {
            ClassDeclaration decl = f.newClassDeclaration(
                    new JavadocBuilder(f)
                        .text("TSVファイルなどのレコードを表すファイルを入力として<code>{0}</code>を読み出す",
                                model.getName())
                        .toJavadoc(),
                    new AttributeBuilder(f)
                        .annotation(context.resolve(SuppressWarnings.class),
                                Models.toLiteral(f, "deprecation"))
                        .Public()
                        .Final()
                        .toAttributes(),
                    context.getTypeName(),
                    Collections.<TypeParameterDeclaration>emptyList(),
                    null,
                    Collections.singletonList(f.newParameterizedType(
                            context.resolve(ModelInput.class),
                            context.resolve(model.getSymbol()))),
                    createMembers());
            context.emit(decl);
        }

        private List<TypeBodyDeclaration> createMembers() {
            List<TypeBodyDeclaration> results = new ArrayList<TypeBodyDeclaration>();
            results.add(createParserField());
            results.add(createConstructor());
            results.add(createReader());
            results.add(createCloser());
            return results;
        }


        private TypeBodyDeclaration createParserField() {
            return f.newFieldDeclaration(
                    null,
                    new AttributeBuilder(f)
                        .Private()
                        .Final()
                        .toAttributes(),
                    context.resolve(RecordParser.class),
                    createParserFieldName(),
                    null);
        }

        private TypeBodyDeclaration createConstructor() {
            return f.newConstructorDeclaration(
                    new JavadocBuilder(f)
                        .text("インスタンスを生成する。")
                        .param(createParserFieldName())
                            .text("利用するパーサー")
                        .exception(context.resolve(IllegalArgumentException.class))
                            .text("引数に<code>null</code>が指定された場合")
                        .toJavadoc(),
                    new AttributeBuilder(f)
                        .Public()
                        .toAttributes(),
                    context.getTypeName(),
                    Collections.singletonList(f.newFormalParameterDeclaration(
                            context.resolve(RecordParser.class),
                            createParserFieldName())),
                    createConstructorBody());
        }

        private List<Statement> createConstructorBody() {
            List<Statement> results = new ArrayList<Statement>();
            results.add(f.newIfStatement(
                    new ExpressionBuilder(f, createParserFieldName())
                        .apply(InfixOperator.EQUALS, Models.toNullLiteral(f))
                        .toExpression(),
                    f.newBlock(new TypeBuilder(f, context.resolve(IllegalArgumentException.class))
                        .newObject(Models.toLiteral(f, createParserFieldName().getToken()))
                        .toThrowStatement())));
            results.add(new ExpressionBuilder(f, f.newThis(null))
                .field(createParserFieldName())
                .assignFrom(createParserFieldName())
                .toStatement());
            return results;
        }

        private MethodDeclaration createReader() {
            return f.newMethodDeclaration(
                    null,
                    new AttributeBuilder(f)
                        .annotation(context.resolve(Override.class))
                        .Public()
                        .toAttributes(),
                    Collections.<TypeParameterDeclaration>emptyList(),
                    context.resolve(boolean.class),
                    f.newSimpleName("readTo"),
                    Collections.singletonList(f.newFormalParameterDeclaration(
                            context.resolve(model.getSymbol()),
                            createModelParameterName())),
                    0,
                    Collections.singletonList(context.resolve(IOException.class)),
                    f.newBlock(createReaderBody()));
        }

        private List<Statement> createReaderBody() {
            List<Statement> results = new ArrayList<Statement>();

            results.add(f.newIfStatement(
                    new ExpressionBuilder(f, createParserFieldName())
                        .method("next")
                        .apply(InfixOperator.EQUALS, Models.toLiteral(f, false))
                        .toExpression(),
                    f.newBlock(new ExpressionBuilder(f, Models.toLiteral(f, false))
                        .toReturnStatement())));

            for (PropertyDeclaration property : model.getDeclaredProperties()) {
                results.add(createReaderStatement(property));
            }
            results.add(f.newReturnStatement(Models.toLiteral(f, true)));
            return results;
        }

        private Statement createReaderStatement(PropertyDeclaration property) {
            assert property != null;
            SimpleName optionGetterName = context.getOptionGetterName(property);
            Expression option = new ExpressionBuilder(f, createModelParameterName())
                .method(optionGetterName)
                .toExpression();
            Statement fill = new ExpressionBuilder(f, createParserFieldName())
                .method("fill", option)
                .toStatement();
            return fill;
        }

        private TypeBodyDeclaration createCloser() {
            return f.newMethodDeclaration(
                    null,
                    new AttributeBuilder(f)
                        .annotation(context.resolve(Override.class))
                        .Public()
                        .toAttributes(),
                    Collections.<TypeParameterDeclaration>emptyList(),
                    context.resolve(void.class),
                    f.newSimpleName("close"),
                    Collections.<FormalParameterDeclaration>emptyList(),
                    0,
                    Collections.singletonList(context.resolve(IOException.class)),
                    f.newBlock(createCloserBody()));
        }

        private List<Statement> createCloserBody() {
            List<Statement> results = new ArrayList<Statement>();
            results.add(new ExpressionBuilder(f, createParserFieldName())
                .method("close")
                .toStatement());
            return results;
        }

        private SimpleName createParserFieldName() {
            return f.newSimpleName("parser");
        }

        private SimpleName createModelParameterName() {
            return f.newSimpleName("model");
        }
    }
}
