package com.rbkmoney.magista_dsl.core.builder;

import com.rbkmoney.magista_dsl.core.Query;
import com.rbkmoney.magista_dsl.core.parser.QueryPart;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public interface QueryBuilder {

    Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException;

    boolean apply(List<QueryPart> queryParts, QueryPart parent);
}
