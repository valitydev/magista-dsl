package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 29.08.16.
 */
public interface QueryProcessor<S, R> {
    R processQuery(S source) throws BadTokenException, QueryProcessingException;
}
