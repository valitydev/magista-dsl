package dev.vality.magista.dsl.builder;

import dev.vality.magista.dsl.Query;
import dev.vality.magista.dsl.parser.QueryPart;

import java.util.List;

public interface QueryBuilder {

    Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException;

    boolean apply(List<QueryPart> queryParts, QueryPart parent);
}
