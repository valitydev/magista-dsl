package dev.vality.magista.dsl.builder;

import dev.vality.magista.dsl.QueryProcessingException;

public class QueryBuilderException extends QueryProcessingException {
    public QueryBuilderException() {
    }

    public QueryBuilderException(String message) {
        super(message);
    }

    public QueryBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryBuilderException(Throwable cause) {
        super(cause);
    }

    public QueryBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
