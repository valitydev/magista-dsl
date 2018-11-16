package com.rbkmoney.magista_dsl.core.builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista_dsl.core.DummiesFunction;
import com.rbkmoney.magista_dsl.core.Query;
import com.rbkmoney.magista_dsl.core.RootQuery;
import com.rbkmoney.magista_dsl.core.parser.BaseQueryParser;
import com.rbkmoney.magista_dsl.core.parser.JsonQueryParser;
import com.rbkmoney.magista_dsl.core.parser.QueryPart;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QueryBuilderImplTest {
    JsonQueryParser parser = new JsonQueryParser() {
        @Override
        protected ObjectMapper getMapper() {
            ObjectMapper mapper = super.getMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper;
        }
    }.withQueryParser(new BaseQueryParser(Arrays.asList(new RootQuery.RootParser(), new DummiesFunction.DummiesParser())) {
        @Override
        public boolean apply(Map<String, Object> source, QueryPart parent) {
            return true;
        }
    });

    private QueryBuilder builder = new BaseQueryBuilder(Arrays.asList(new RootQuery.RootBuilder(), new DummiesFunction.DummiesBuilder())) {
        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return true;
        }
    };

    @Test
    public void test() {
        String json = "{'query': {'dummies': {'id': '1','kek':'kek','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        Query query = buildQuery(json);
        assertTrue(query instanceof RootQuery);
        query.getDescriptor();
    }

    @Test(expected = QueryBuilderException.class)
    public void testNoFunctionParse() {
        String json = "{'query': {'payments_geo_stat1': {}}}";
        Query query = buildQuery(json);
        fail("no functions in oot query, should not reach this point");
    }

    Query buildQuery(String json) {
        List<QueryPart> queryParts = parser.parseQuery(json);
        return builder.buildQuery(queryParts, null, null, null);
    }
}
