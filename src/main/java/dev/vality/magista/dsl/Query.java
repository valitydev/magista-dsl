package dev.vality.magista.dsl;

public interface Query<T, CT> {
    Object getDescriptor();

    Query getParentQuery();

    void setParentQuery(Query query);

    QueryParameters getQueryParameters();

    QueryResult<T, CT> execute(QueryContext context) throws QueryExecutionException;


}
