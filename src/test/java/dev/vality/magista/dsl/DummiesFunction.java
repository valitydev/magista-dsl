package dev.vality.magista.dsl;

import dev.vality.magista.dsl.builder.AbstractQueryBuilder;
import dev.vality.magista.dsl.builder.QueryBuilder;
import dev.vality.magista.dsl.builder.QueryBuilderException;
import dev.vality.magista.dsl.parser.AbstractQueryParser;
import dev.vality.magista.dsl.parser.QueryParserException;
import dev.vality.magista.dsl.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DummiesFunction extends PagedBaseFunction<Map.Entry<Long, Dummy>, StatTestResponse>
        implements CompositeQuery<Map.Entry<Long, Dummy>, StatTestResponse> {

    public static final String FUNC_NAME = "dummies";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private DummiesFunction(Object descriptor, QueryParameters params, String continuationToken,
                            CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    private static DummiesFunction createDummiesFunction(Object descriptor, QueryParameters queryParameters,
                                                         String continuationToken,
                                                         CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        DummiesFunction dummiesFunction = new DummiesFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(dummiesFunction);
        return dummiesFunction;
    }

    @Override
    public QueryResult<Map.Entry<Long, Dummy>, StatTestResponse> execute(QueryContext context)
            throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, Dummy>, StatTestResponse> execute(QueryContext context,
                                                                         List<QueryResult> collectedResults)
            throws QueryExecutionException {
        QueryResult<Map.Entry<Long, Dummy>, List<Map.Entry<Long, Dummy>>> dummiesResult =
                (QueryResult<Map.Entry<Long, Dummy>, List<Map.Entry<Long, Dummy>>>) collectedResults.get(0);

        return new BaseQueryResult<>(() -> dummiesResult.getDataStream(), () -> {
            List<Dummy> statResponseData =
                    dummiesResult.getDataStream().map(dummyEntry -> dummyEntry.getValue()).collect(Collectors.toList());
            StatTestResponse statResponse = new StatTestResponse(statResponseData);
            List<Map.Entry<Long, Dummy>> dummyStats = dummiesResult.getCollectedStream();
            if (!dummiesResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == dummyStats.size()) {
                statResponse.setContinuationToken(
                        TokenUtil.buildToken(getQueryParameters(), dummyStats.get(dummyStats.size() - 1).getKey()));
            }
            return statResponse;
        });
    }

    @Override
    public TestParameters getQueryParameters() {
        return (TestParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new TestParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class TestParameters extends PagedBaseParameters {

        public TestParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public TestParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getId() {
            return getStringParameter("id", false);
        }

        public String getKek() {
            return getStringParameter("kek", false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter("from_time", false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter("to_time", false);
        }

    }

    public static class DummiesValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            TestParameters testParameters = super.checkParamsType(parameters, TestParameters.class);

            String kek = testParameters.getKek();
            if (kek != null && !kek.matches("kek")) {
                checkParamsResult(true, "kek", RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            validateTimePeriod(testParameters.getFromTime(), testParameters.getToTime());
        }
    }

    public static class DummiesParser extends AbstractQueryParser {
        private final DummiesValidator validator = new DummiesValidator();

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            TestParameters parameters = getValidatedParameters(funcSource, parent, TestParameters::new, validator);

            return Stream.of(new QueryPart(FUNC_NAME, parameters, parent)).collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map source, QueryPart parent) {
            return parent != null && RootQuery.RootParser.getMainDescriptor().equals(parent.getDescriptor())
                    && (source.get(FUNC_NAME) instanceof Map);
        }
    }

    public static class DummiesBuilder extends AbstractQueryBuilder {
        private final DummiesValidator validator = new DummiesValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart,
                                QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(DummiesParser.getMainDescriptor(), queryParts,
                    queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME,
                            queryPart.getParameters(), continuationToken));
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery =
                    createCompositeQuery(queryPart.getDescriptor(), getParameters(queryPart.getParent()), queries);
            return createDummiesFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken,
                    compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(DummiesParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static class GetDataFunction
            extends PagedBaseFunction<Map.Entry<Long, Dummy>, Collection<Map.Entry<Long, Dummy>>> {
        private static final String FUNC_NAME = DummiesFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        protected TestQueryContext getContext(QueryContext context) {
            return super.getContext(context, TestQueryContext.class);
        }

        @Override
        public QueryResult<Map.Entry<Long, Dummy>, Collection<Map.Entry<Long, Dummy>>> execute(QueryContext context)
                throws QueryExecutionException {
            TestQueryContext functionContext = getContext(context);
            TestParameters parameters =
                    new TestParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            Collection<Map.Entry<Long, Dummy>> result = functionContext.getSearchDao().getDummies(parameters.getId());
            return new BaseQueryResult<>(() -> result.stream(), () -> result);

        }
    }

}
