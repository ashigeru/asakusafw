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
package com.asakusafw.info.hive;

import org.junit.Test;

import com.asakusafw.info.InfoSerDe;

/**
 * Test for {@link FieldType}.
 */
public class FieldTypeTest {

    /**
     * plain type.
     */
    @Test
    public void plain() {
        for (FieldType.TypeName name : FieldType.TypeName.values()) {
            if (name.getCategory() == FieldType.Category.PLAIN) {
                check(PlainType.of(name));
            }
        }
    }

    /**
     * sequence type.
     */
    @Test
    public void sequence() {
        for (FieldType.TypeName name : FieldType.TypeName.values()) {
            if (name.getCategory() == FieldType.Category.SEQUENCE) {
                check(new SequenceType(name, 10));
            }
        }
    }

    /**
     * decimal type.
     */
    @Test
    public void decimal() {
        for (FieldType.TypeName name : FieldType.TypeName.values()) {
            if (name.getCategory() == FieldType.Category.DECIMAL) {
                check(new DecimalType(name, 10, 2));
            }
        }
    }

    /**
     * array type.
     */
    @Test
    public void array() {
        check(new ArrayType(PlainType.of(FieldType.TypeName.INT)));
    }

    /**
     * map type.
     */
    @Test
    public void map() {
        check(new MapType(PlainType.of(FieldType.TypeName.INT), PlainType.of(FieldType.TypeName.STRING)));
    }

    /**
     * union type.
     */
    @Test
    public void union() {
        check(new UnionType(
                PlainType.of(FieldType.TypeName.INT),
                PlainType.of(FieldType.TypeName.STRING),
                new ArrayType(PlainType.of(FieldType.TypeName.DOUBLE))));
    }

    /**
     * struct type.
     */
    @Test
    public void struct() {
        check(new StructType(
                new ColumnInfo("a", PlainType.of(FieldType.TypeName.INT)),
                new ColumnInfo("b", PlainType.of(FieldType.TypeName.STRING)),
                new ColumnInfo("c", new ArrayType(PlainType.of(FieldType.TypeName.DOUBLE)))));
    }

    private static void check(FieldType value) {
        InfoSerDe.checkRestore(FieldType.class, value);
    }
}
