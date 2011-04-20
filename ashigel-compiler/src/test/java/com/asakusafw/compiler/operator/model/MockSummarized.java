package com.asakusafw.compiler.operator.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.asakusafw.runtime.model.DataModel;
import com.asakusafw.runtime.value.IntOption;
import com.asakusafw.runtime.value.LongOption;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.model.Summarized;

/**
 * mock_summarizedを表すデータモデルクラス。
 */
@Summarized(term = @Summarized.Term(source = MockHoge.class, foldings = {
        @Summarized.Folding(aggregator = Summarized.Aggregator.ANY, source = "value", destination = "key"),
        @Summarized.Folding(aggregator = Summarized.Aggregator.COUNT, source = "value", destination = "count") }, shuffle = @Key(group = { "value" })))
public class MockSummarized implements DataModel<MockSummarized>, Writable {
    private final IntOption key = new IntOption();
    private final LongOption count = new LongOption();

    @Override
    @SuppressWarnings("deprecation")
    public void reset() {
        this.key.setNull();
        this.count.setNull();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void copyFrom(MockSummarized other) {
        this.key.copyFrom(other.key);
        this.count.copyFrom(other.count);
    }

    /**
     * keyを返す。
     *
     * @return key
     * @throws NullPointerException
     *             keyの値が<code>null</code>である場合
     */
    public int getKey() {
        return this.key.get();
    }

    /**
     * keyを設定する。
     *
     * @param value
     *            設定する値
     */
    @SuppressWarnings("deprecation")
    public void setKey(int value) {
        this.key.modify(value);
    }

    /**
     * <code>null</code>を許すkeyを返す。
     *
     * @return key
     */
    public IntOption getKeyOption() {
        return this.key;
    }

    /**
     * keyを設定する。
     *
     * @param option
     *            設定する値、<code>null</code>の場合にはこのプロパティが<code>null</code>を表すようになる
     */
    @SuppressWarnings("deprecation")
    public void setKeyOption(IntOption option) {
        this.key.copyFrom(option);
    }

    /**
     * countを返す。
     *
     * @return count
     * @throws NullPointerException
     *             countの値が<code>null</code>である場合
     */
    public long getCount() {
        return this.count.get();
    }

    /**
     * countを設定する。
     *
     * @param value
     *            設定する値
     */
    @SuppressWarnings("deprecation")
    public void setCount(long value) {
        this.count.modify(value);
    }

    /**
     * <code>null</code>を許すcountを返す。
     *
     * @return count
     */
    public LongOption getCountOption() {
        return this.count;
    }

    /**
     * countを設定する。
     *
     * @param option
     *            設定する値、<code>null</code>の場合にはこのプロパティが<code>null</code>を表すようになる
     */
    @SuppressWarnings("deprecation")
    public void setCountOption(LongOption option) {
        this.count.copyFrom(option);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append("class=mock_summarized");
        result.append(", key=");
        result.append(this.key);
        result.append(", count=");
        result.append(this.count);
        result.append("}");
        return result.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + count.hashCode();
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MockSummarized other = (MockSummarized) obj;
        if (this.key.equals(other.key) == false) {
            return false;
        }
        if (this.count.equals(other.count) == false) {
            return false;
        }
        return true;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        key.write(out);
        count.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        key.readFields(in);
        count.readFields(in);
    }
}