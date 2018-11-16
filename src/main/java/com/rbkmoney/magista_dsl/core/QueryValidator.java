package com.rbkmoney.magista_dsl.core;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public interface QueryValidator {
    void validateParameters(QueryParameters parameters) throws IllegalArgumentException;

    default void validateQuery(Query query) throws IllegalArgumentException {
        validateParameters(query.getQueryParameters());
    }
}
