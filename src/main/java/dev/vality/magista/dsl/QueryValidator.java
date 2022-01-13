package dev.vality.magista.dsl;

public interface QueryValidator {
    void validateParameters(QueryParameters parameters) throws IllegalArgumentException;

    default void validateQuery(Query query) throws IllegalArgumentException {
        validateParameters(query.getQueryParameters());
    }
}
