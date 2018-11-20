package com.rbkmoney.magista.dsl;

import java.util.List;

public interface CompositeQuery<T, CT> extends Query<T, CT> {
    List<Query> getChildQueries();

    default boolean isParallel() {
        return false;
    }

    default QueryResult<T, CT> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        throw new UnsupportedOperationException("Explicit implementation required");
    }

}
