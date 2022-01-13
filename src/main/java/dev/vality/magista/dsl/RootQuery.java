package dev.vality.magista.dsl;

import dev.vality.magista.dsl.builder.AbstractQueryBuilder;
import dev.vality.magista.dsl.builder.QueryBuilder;
import dev.vality.magista.dsl.builder.QueryBuilderException;
import dev.vality.magista.dsl.parser.AbstractQueryParser;
import dev.vality.magista.dsl.parser.QueryParserException;
import dev.vality.magista.dsl.parser.QueryPart;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootQuery extends BaseQuery {
    private final Query childQuery;

    private RootQuery(Object descriptor, QueryParameters params, Query childQuery) {
        super(descriptor, params);
        this.childQuery = childQuery;
    }

    @Override
    public QueryResult execute(QueryContext context) throws QueryExecutionException {
        return childQuery.execute(context);
    }

    public static class RootParameters extends QueryParameters {
        public RootParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public RootParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public Map getQuery() {
            Object obj = getParameter(Parameters.QUERY_PARAMETER, false);
            return obj instanceof Map ? (Map) obj : null;
        }

        public QueryParameters getRestParameters() {
            return removeParameters(QueryParameters::new, Parameters.QUERY_PARAMETER);
        }
    }

    public static class RootValidator extends BaseQueryValidator {
        public static final String DEFAULT_ERR_MSG_STRING = "invalid or not found";

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            RootParameters rootParameters = super.checkParamsType(parameters, RootParameters.class);
            if (rootParameters.getQuery() == null) {
                checkParamsResult(true, Parameters.QUERY_PARAMETER, DEFAULT_ERR_MSG_STRING);
            }
        }

        @Override
        public void validateQuery(Query query) throws IllegalArgumentException {
            if (query instanceof RootQuery) {
                Query childQuery = ((RootQuery) query).childQuery;
                if (childQuery instanceof CompositeQuery) {
                    Optional<? extends Collection> childQueries = Optional.ofNullable(((CompositeQuery) childQuery).getChildQueries());
                    checkParamsResult(
                            !childQueries.isPresent() || childQueries.get().isEmpty(),
                            "Request must contain at least one query"
                    );
                }
            } else if (query instanceof CompositeQuery) {
                checkParamsResult(true, "Request can't hold more than one query, received count: " + ((CompositeQuery) query).getChildQueries().size());
            } else {
                assert false : "No other types expected here";
            }
        }
    }

    public static class RootParser extends AbstractQueryParser {
        private RootValidator validator = new RootValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            RootParameters parameters = getValidatedParameters(source, parent, RootParameters::new, validator);

            return Stream.of(
                    new QueryPart(getMainDescriptor(), new QueryParameters(parameters.getQuery(), parameters), parent),
                    new QueryPart(QueryPart.DEFAULT_DESCRIPTOR, parameters.getRestParameters(), parent))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map source, QueryPart parent) {
            return parent == null;
        }

        public static String getMainDescriptor() {
            return Parameters.QUERY_PARAMETER;
        }
    }

    public static class RootBuilder extends AbstractQueryBuilder {
        private RootValidator validator = new RootValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(RootParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken, baseBuilder));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private RootQuery createQuery(QueryPart queryPart, String continuationToken, QueryBuilder baseBuilder) {
            Query childQuery = baseBuilder.buildQuery(queryPart.getChildren(), continuationToken, queryPart, baseBuilder);
            RootQuery rootQuery = new RootQuery(queryPart.getDescriptor(), queryPart.getParameters(), childQuery);
            childQuery.setParentQuery(rootQuery);
            return rootQuery;
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(RootParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }

    }
}
