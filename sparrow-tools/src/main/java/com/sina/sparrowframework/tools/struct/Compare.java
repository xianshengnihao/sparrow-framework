package com.sina.sparrowframework.tools.struct;


/**
 * created  on 2019-02-23.
 */
public interface Compare<T> {

    CompareResult compareWith(T o);

    interface Comparer {

        boolean eq();

        boolean lt();

        boolean le();

        boolean gt();

        boolean ge();

    }

}
