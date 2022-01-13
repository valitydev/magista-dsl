package dev.vality.magista.dsl.parser;


import java.util.List;

public interface QueryParser<T> {
    List<QueryPart> parseQuery(T source, QueryPart parent) throws QueryParserException;

    boolean apply(T source, QueryPart parent);
}
