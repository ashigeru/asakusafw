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
package com.asakusafw.runtime.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;

import com.asakusafw.runtime.io.util.WritableRawComparable;

/**
 * Represents a {@code float} value which can be {@code null}.
 */
public final class FloatOption extends ValueOption<FloatOption> {

    private float value;

    /**
     * Creates a new instance which represents {@code null} value.
     */
    public FloatOption() {
        this.nullValue = true;
    }

    /**
     * Creates a new instance which represents the specified value.
     * @param value the initial value
     */
    public FloatOption(float value) {
        this.value = value;
        this.nullValue = false;
    }

    /**
     * Returns the value which this object represents.
     * @return the value which this object represents, never {@code null}
     * @throws NullPointerException if this object represents {@code null}
     */
    public float get() {
        if (nullValue) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * Returns the value which this object represents.
     * @param alternate the alternative value for {@code null}
     * @return the value which this object represents, or the alternative one if this object represents {@code null}
     */
    public float or(float alternate) {
        if (nullValue) {
            return alternate;
        }
        return value;
    }

    /**
     * Adds a value into this object.
     * @param delta the value to be add
     * @throws NullPointerException if this object represents {@code null}
     */
    public void add(float delta) {
        if (nullValue) {
            throw new NullPointerException();
        }
        this.value += delta;
    }

    /**
     * Adds a value into this object.
     * @param other the value to be add, or {@code null} to do nothing
     * @throws NullPointerException if this object represents {@code null}
     */
    public void add(FloatOption other) {
        if (nullValue) {
            throw new NullPointerException();
        }
        if (other.nullValue) {
            return;
        }
        this.value += other.value;
    }

    /**
     * Sets the value.
     * @param newValue the value
     * @return this
     * @see ValueOption#setNull()
     * @deprecated Application developer should not use this method directly
     */
    @Deprecated
    public FloatOption modify(float newValue) {
        this.nullValue = false;
        this.value = newValue;
        return this;
    }

    @Override
    @Deprecated
    public void copyFrom(FloatOption optionOrNull) {
        if (optionOrNull == null || optionOrNull.nullValue) {
            this.nullValue = true;
        } else {
            this.nullValue = false;
            this.value = optionOrNull.value;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        if (isNull()) {
            return 1;
        }
        int result = 1;
        result = prime * result + Float.floatToIntBits(result);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FloatOption other = (FloatOption) obj;
        if (nullValue != other.nullValue) {
            return false;
        }
        if (nullValue == false
                && Float.floatToIntBits(value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether both this object and the specified value represents an equivalent value or not.
     * @param other the target value
     * @return {@code true} if this object has the specified value, otherwise {@code false}
     */
    public boolean has(float other) {
        if (isNull()) {
            return false;
        }
        return Float.floatToIntBits(value) == Float.floatToIntBits(other);
    }

    @Override
    public int compareTo(WritableRawComparable o) {
        FloatOption other = (FloatOption) o;
        if (nullValue | other.nullValue) {
            if (nullValue & other.nullValue) {
                return 0;
            }
            return nullValue ? -1 : +1;
        }
        int left = encode(value) - Integer.MIN_VALUE;
        int right = encode(other.value) - Integer.MIN_VALUE;
        if (left == right) {
            return 0;
        }
        if (left < right) {
            return -1;
        }
        return +1;
    }

    @Override
    public String toString() {
        if (isNull()) {
            return String.valueOf((Object) null);
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        if (isNull()) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(encode(value));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void readFields(DataInput in) throws IOException {
        if (in.readBoolean()) {
            modify(decode(in.readInt()));
        } else {
            setNull();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int restore(byte[] bytes, int offset, int limit) throws IOException {
        if (limit - offset == 0) {
            throw new IOException(MessageFormat.format(
                    "Cannot restore a float field ({0})",
                    "invalid length"));
        }
        if (bytes[offset + 0] == 0) {
            setNull();
            return 1;
        } else if (limit - offset >= 1 + 1) {
            modify(decode(ByteArrayUtil.readInt(bytes, offset + 1)));
            return 4 + 1;
        } else {
            throw new IOException(MessageFormat.format(
                    "Cannot restore a float field ({0})",
                    "invalid length"));
        }
    }

    @Override
    public int getSizeInBytes(byte[] buf, int offset) throws IOException {
        return getBytesLength(buf, offset, buf.length - offset);
    }

    @Override
    public int compareInBytes(byte[] b1, int o1, byte[] b2, int o2) throws IOException {
        return compareBytes(b1, o1, b1.length - o1, b2, o2, b2.length - o2);
    }

    /**
     * Returns the actual number of bytes from the serialized byte array.
     * @param bytes the target byte array
     * @param offset the beginning index in the byte array (inclusive)
     * @param length the limit length of the byte array
     * @return the comparison result
     */
    public static int getBytesLength(byte[] bytes, int offset, int length) {
        return bytes[offset] == 0 ? 1 : 5;
    }

    /**
     * Compares between the two objects in serialized form.
     * @param b1 the first byte array to be compared
     * @param s1 the beginning index in {@code b1}
     * @param l1 the limit byte size in {@code b1}
     * @param b2 the second byte array to be compared
     * @param s2 the beginning index in {@code b2}
     * @param l2 the limit byte size in {@code b2}
     * @return the comparison result
     */
    public static int compareBytes(
            byte[] b1, int s1, int l1,
            byte[] b2, int s2, int l2) {
        int len1 = getBytesLength(b1, s1, l1);
        int len2 = getBytesLength(b2, s2, l2);
        return ByteArrayUtil.compare(b1, s1, len1, b2, s2, len2);
    }

    private static int encode(float decoded) {
        int bits = Float.floatToIntBits(decoded);
        bits ^= Integer.MIN_VALUE | (bits >> Integer.SIZE - 1);
        return bits;
    }

    private static float decode(int encoded) {
        int bits = encoded;
        bits ^= Integer.MIN_VALUE | ~(bits >> Integer.SIZE - 1);
        return Float.intBitsToFloat(bits);
    }
}
