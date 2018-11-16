package com.rbkmoney.magista.dsl.builder;

import com.rbkmoney.magista.dsl.Query;
import com.rbkmoney.magista.dsl.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public interface QueryBuilder {

    Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException;

    boolean apply(List<QueryPart> queryParts, QueryPart parent);
}
