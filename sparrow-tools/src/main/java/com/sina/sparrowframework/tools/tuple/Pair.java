/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sina.sparrowframework.tools.tuple;

import com.sina.sparrowframework.tools.utility.CompareBuilder;
import com.sina.sparrowframework.tools.utility.ObjectToolkit;

import java.io.Serializable;
import java.util.Map;

/**
 * 普通泛型（2属性）
 * @param <L>
 * @param <R>
 */
public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 4954918890077093841L;


    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new ImmutablePair<L, R>(left, right);
    }


    public abstract L getLeft();


    public abstract R getRight();


    @Override
    public final L getKey() {
        return getLeft();
    }


    @Override
    public R getValue() {
        return getRight();
    }


    @Override
    public int compareTo(final Pair<L, R> other) {
      return new CompareBuilder().append(getLeft(), other.getLeft())
              .append(getRight(), other.getRight()).toComparison();
    }


    @SuppressWarnings( "deprecation" ) // ObjectToolkit.equals(Object, Object) has been deprecated in 3.2
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return ObjectToolkit.equals(getKey(), other.getKey())
                    && ObjectToolkit.equals(getValue(), other.getValue());
        }
        return false;
    }


    @Override
    public int hashCode() {
        // see Map.Entry API specification
        return (getKey() == null ? 0 : getKey().hashCode()) ^
                (getValue() == null ? 0 : getValue().hashCode());
    }


    @Override
    public String toString() {
        return new StringBuilder().append('(').append(getLeft()).append(',').append(getRight()).append(')').toString();
    }


    public String toString(final String format) {
        return String.format(format, getLeft(), getRight());
    }

}
