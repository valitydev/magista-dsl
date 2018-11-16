package com.rbkmoney.magista_dsl.core.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista_dsl.core.DummiesFunction;
import com.rbkmoney.magista_dsl.core.RootQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JsonQueryParserTest {

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

    @Test
    public void testNoFunctionParse() {
        String json = "{'query': {'keksiki': {}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        assertEquals("root query", 1, queryParts.size());
        assertEquals("root query has 0 parameter - no recognized function names", 0, queryParts.get(0).getChildren().size());

    }

    @Test(expected = QueryParserException.class)
    public void testNoQueryParse() {
        String json = "{'query1': {'dummies': {}}}";
        List<QueryPart> queryParts = parser.parseQuery(json);
        fail("no root query, should not reach this point");
    }

    @Test(expected = QueryParserException.class)
    public void testKekParseError() {
        String json = "{'query': {'dummies': {'id': '1','kek': 'kekekekkekekekke','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test
    public void testHappyPath() {
        String json = "{'query': {'dummies': {'id': '1','kek': 'kek','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T01:12:00Z'}}}";
        parser.parseQuery(json);
    }

    @Test(expected = QueryParserException.class)
    public void testTimeParseError() {
        String json = "{'query': {'dummies': {'id': '1','kek': '2','from_time': '2016-03-22T00:12:00Z','to_time': '2016-03-22T00:00:00Z'}}}";
        parser.parseQuery(json);
    }
}
