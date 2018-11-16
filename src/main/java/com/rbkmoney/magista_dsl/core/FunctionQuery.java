package com.rbkmoney.magista_dsl.core;


/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface FunctionQuery<T, CT> extends Query<T, CT> {

    String getName();
}
