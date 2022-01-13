package dev.vality.magista.dsl.parser;


import java.util.List;

public interface QueryParser<Src> {
    List<QueryPart> parseQuery(Src source, QueryPart parent) throws QueryParserException;
    boolean apply(Src source, QueryPart parent);
}
