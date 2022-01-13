package dev.vality.magista.dsl;

public interface QueryProcessor<S, R> {
    R processQuery(S source) throws BadTokenException, QueryProcessingException;
}
