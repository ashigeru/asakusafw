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
package com.asakusafw.testdriver;

import java.io.File;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asakusafw.runtime.directio.DataFormat;
import com.asakusafw.testdriver.core.DataModelDefinition;
import com.asakusafw.testdriver.core.DataModelSourceFactory;
import com.asakusafw.testdriver.core.DataModelSourceFilter;
import com.asakusafw.testdriver.core.TestDataToolProvider;
import com.asakusafw.utils.io.Provider;
import com.asakusafw.utils.io.Source;

/**
 * An abstract super class which represents an input port of data flow on testing.
 * Clients should not inherit this class directly.
 * @param <T> the input data model type
 * @param <S> the implementation class type
 * @since 0.6.0
 * @version 0.9.1
 */
public abstract class FlowDriverInput<T, S extends FlowDriverInput<T, S>> extends DriverInputBase<T> {

    private static final Logger LOG = LoggerFactory.getLogger(FlowDriverInput.class);

    /**
     * Creates a new instance.
     * @param callerClass the current context class
     * @param testTools the test data tools
     * @param name the original input name
     * @param modelType the data model type
     * @since 0.6.0
     */
    public FlowDriverInput(Class<?> callerClass, TestDataToolProvider testTools, String name, Class<T> modelType) {
        super(callerClass, testTools, name, modelType);
    }

    /**
     * Returns this object.
     * @return this
     * @since 0.6.0
     */
    protected abstract S getThis();

    /**
     * Sets the test data set for this input.
     * @param sourcePath the path to test data set
     * @return this
     * @throws IllegalArgumentException if the source was not found on the path
     * @since 0.2.0
     */
    public S prepare(String sourcePath) {
        return prepare(sourcePath, null);
    }

    /**
     * Sets the test data set for this input.
     * @param sourcePath the path to test data set
     * @param transformer the input source transformer
     * @return this
     * @throws IllegalArgumentException if the source was not found on the path
     * @since 0.10.2
     */
    public S prepare(String sourcePath, Consumer<? super T> transformer) {
        if (sourcePath == null) {
            throw new IllegalArgumentException("sourcePath must not be null"); //$NON-NLS-1$
        }
        DataModelSourceFactory factory = toDataModelSourceFactory(sourcePath);
        return prepare(factory, transformer);
    }

    /**
     * Sets the test data set for this input.
     * @param factory factory which provides test data set
     * @return this
     * @since 0.6.0
     */
    public S prepare(DataModelSourceFactory factory) {
        return prepare(factory, null);
    }

    /**
     * Sets the test data set for this input.
     * @param factory factory which provides test data set
     * @param transformer the input source transformer
     * @return this
     * @since 0.10.2
     */
    public S prepare(DataModelSourceFactory factory, Consumer<? super T> transformer) {
        if (factory == null) {
            throw new IllegalArgumentException("factory must not be null"); //$NON-NLS-1$
        }
        LOG.debug("prepare - ModelType: {}", getModelType()); //$NON-NLS-1$
        if (transformer == null) {
            setSource(factory);
        } else {
            DataModelSourceFilter filter = toDataModelSourceFilter(transformer);
            setSource(filter.apply(factory));
        }
        return getThis();
    }

    /**
     * Sets the test data set for this input.
     * @param objects the test data objects
     * @return this
     * @since 0.6.0
     */
    public S prepare(Iterable<? extends T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("objects must not be null"); //$NON-NLS-1$
        }
        return prepare(toDataModelSourceFactory(objects));
    }

    /**
     * Sets the test data set for this input.
     * @param provider the test data set provider
     * @return this
     * @since 0.6.0
     */
    public S prepare(Provider<? extends Source<? extends T>> provider) {
        if (provider == null) {
            throw new IllegalArgumentException("objects must not be null"); //$NON-NLS-1$
        }
        return prepare(toDataModelSourceFactory(provider));
    }

    /**
     * Sets the test data set for this input.
     * Note that, the original source path may be changed if tracking source file name.
     * To keep the source file path information, please use {@link #prepare(Class, File)} instead.
     * @param formatClass the data format class
     * @param sourcePath the input file path on the class path
     * @return this
     * @throws IllegalArgumentException if the source is not valid for the given data format
     * @since 0.9.1
     */
    public S prepare(Class<? extends DataFormat<? super T>> formatClass, String sourcePath) {
        return prepare(formatClass, sourcePath, null);
    }

    /**
     * Sets the test data set for this input.
     * Note that, the original source path may be changed if tracking source file name.
     * To keep the source file path information, please use {@link #prepare(Class, File)} instead.
     * @param formatClass the data format class
     * @param sourcePath the input file path on the class path
     * @param transformer the input source transformer
     * @return this
     * @throws IllegalArgumentException if the source is not valid for the given data format
     * @since 0.10.2
     */
    public S prepare(
            Class<? extends DataFormat<? super T>> formatClass, String sourcePath, Consumer<? super T> transformer) {
        DataModelDefinition<T> definition = getDataModelDefinition();
        DataModelSourceFactory factory = toDataModelSourceFactory(definition, formatClass, sourcePath);
        return prepare(factory, transformer);
    }

    /**
     * Sets the test data set for this input.
     * @param formatClass the data format class
     * @param sourceFile the input file
     * @return this
     * @throws IllegalArgumentException if the source is not valid for the given data format
     * @since 0.9.1
     */
    public S prepare(Class<? extends DataFormat<? super T>> formatClass, File sourceFile) {
        return prepare(formatClass, sourceFile, null);
    }

    /**
     * Sets the test data set for this input.
     * @param formatClass the data format class
     * @param sourceFile the input file
     * @param transformer the input source transformer
     * @return this
     * @throws IllegalArgumentException if the source is not valid for the given data format
     * @since 0.10.2
     */
    public S prepare(
            Class<? extends DataFormat<? super T>> formatClass, File sourceFile, Consumer<? super T> transformer) {
        DataModelDefinition<T> definition = getDataModelDefinition();
        DataModelSourceFactory factory = toDataModelSourceFactory(definition, formatClass, sourceFile);
        return prepare(factory, transformer);
    }

    /**
     * Configures this object.
     * @param configurator the configurator
     * @return this
     * @since 0.10.2
     */
    public S with(Consumer<? super S> configurator) {
        if (configurator != null) {
            configurator.accept(getThis());
        }
        return getThis();
    }
}
