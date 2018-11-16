package com.rbkmoney.magista_dsl.core.parser;

import com.rbkmoney.magista_dsl.core.QueryParameters;
import com.rbkmoney.magista_dsl.core.QueryValidator;

import java.util.Map;

/**
 * Created by vpankrashkin on 25.08.16.
 */
public abstract class AbstractQueryParser implements QueryParser<Map<String, Object>> {

    protected QueryParameters getParameters(QueryPart queryPart) {
        return queryPart == null ? null : queryPart.getParameters();
    }

    protected <T extends QueryParameters> T getValidatedParameters(Map<String, Object> source, QueryPart parent, QueryParameters.QueryParametersRef<T> parametersRef, QueryValidator validator) throws QueryParserException {
        try {
            T parameters = parametersRef.newInstance(source, getParameters(parent));
            validator.validateParameters(parameters);
            return parameters;
        } catch (IllegalArgumentException e) {
            throw new QueryParserException(e.getMessage(), e);
        }
    }
}
