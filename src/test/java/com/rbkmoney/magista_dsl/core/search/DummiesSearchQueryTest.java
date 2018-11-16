package com.rbkmoney.magista_dsl.core.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista_dsl.core.*;
import com.rbkmoney.magista_dsl.core.builder.BaseQueryBuilder;
import com.rbkmoney.magista_dsl.core.builder.QueryBuilder;
import com.rbkmoney.magista_dsl.core.parser.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DummiesSearchQueryTest {

    private final BaseQueryParser queryPartParser = new BaseQueryParser(Arrays.asList(new RootQuery.RootParser(), new DummiesFunction.DummiesParser())) {
        @Override
        public boolean apply(Map<String, Object> source, QueryPart parent) {
            return true;
        }
    };
    private final BaseQueryBuilder baseQueryBuilder = new BaseQueryBuilder(Arrays.asList(new RootQuery.RootBuilder(), new DummiesFunction.DummiesBuilder())) {
        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return true;
        }
    };
    private final JsonQueryParser jsonQueryParser = new JsonQueryParser() {
        @Override
        protected ObjectMapper getMapper() {
            ObjectMapper mapper = super.getMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper;
        }
    }.withQueryParser(queryPartParser);

    QueryProcessorImpl queryProcessor = new QueryProcessorImpl(jsonQueryParser, baseQueryBuilder, new TestQueryContext(new SearchDao()));

    @Test
    public void testPayments() {
        String json = "{'query': {'dummies': {'id': '1','kek': 'kek','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        StatTestResponse statResponse = queryProcessor.processQuery(new StatTestRequest(json));
        assertEquals(1, statResponse.getStatTestResponseData().getDummies().size());
    }

    @Test(expected = QueryParserException.class)
    public void testWhenSizeOverflow() {
        String json = "{'query': {'dummies': {'size': 1001}}}";
        queryProcessor.processQuery(new StatTestRequest(json));
    }


    @Test(expected = BadTokenException.class)
    public void testBadToken() {
        String json = "{'query': {'dummies': {'id': '1','kek': 'kek','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        StatTestRequest statRequest = new StatTestRequest(json);
        statRequest.setContinuationToken(UUID.randomUUID().toString());
        queryProcessor.processQuery(statRequest);
    }


    class QueryProcessorImpl implements QueryProcessor<StatTestRequest, StatTestResponse> {
        private QueryParser<String> sourceParser;
        private QueryBuilder queryBuilder;
        private QueryContext queryContext;

        public QueryProcessorImpl(QueryParser<String> sourceParser, QueryBuilder queryBuilder, QueryContext queryContext) {
            this.sourceParser = sourceParser;
            this.queryBuilder = queryBuilder;
            this.queryContext = queryContext;
        }

        @Override
        public StatTestResponse processQuery(StatTestRequest source) throws BadTokenException, QueryProcessingException {
            List<QueryPart> queryParts = sourceParser.parseQuery(source.getDsl(), null);
            Query query = queryBuilder.buildQuery(queryParts, source.getContinuationToken(), null, null);
            QueryResult queryResult = query.execute(queryContext);
            Object result = queryResult.getCollectedStream();
            if (result instanceof StatTestResponse) {
                return (StatTestResponse) result;
            } else {
                throw new QueryProcessingException("QueryResult has wrong type: " + result.getClass().getName());
            }
        }
    }
}