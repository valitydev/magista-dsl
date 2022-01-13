package dev.vality.magista.dsl;

import java.util.Map;
import java.util.Optional;

public abstract class PagedBaseFunction<T, CT> extends BaseFunction<T, CT> {

    public static final int MAX_SIZE_VALUE = 1000;

    private String continuationToken;

    public PagedBaseFunction(Object descriptor, QueryParameters params, String name, String continuationToken) {
        super(descriptor, params, name);
        this.continuationToken = continuationToken;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public Optional<Long> getFromId() {
        return TokenUtil.extractIdValue(continuationToken);
    }

    public static class PagedBaseParameters extends QueryParameters {

        public PagedBaseParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PagedBaseParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public Integer getSize() {
            return Optional.ofNullable(getIntParameter(Parameters.SIZE_PARAMETER, true))
                    .orElse(MAX_SIZE_VALUE);
        }

    }

    public static class PagedBaseValidator extends BaseQueryValidator {
        @Override
        public void validateQuery(Query query) throws IllegalArgumentException {
            super.validateQuery(query);
            if (query instanceof PagedBaseFunction) {
                validateContinuationToken(query.getQueryParameters(), ((PagedBaseFunction) query).getContinuationToken());
            }
        }

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PagedBaseParameters pagedBaseParameters = super.checkParamsType(parameters, PagedBaseParameters.class);
            checkParamsResult(pagedBaseParameters.getSize() > MAX_SIZE_VALUE,
                    String.format(
                            "Size must be less or equals to %d but was %d",
                            MAX_SIZE_VALUE,
                            pagedBaseParameters.getSize()
                    )
            );
        }

        private void validateContinuationToken(QueryParameters queryParameters, String continuationToken) throws BadTokenException {
            try {
                TokenUtil.validateToken(queryParameters, continuationToken);
            } catch (IllegalArgumentException ex) {
                throw new BadTokenException("Token validation failure", ex);
            }
        }
    }
}
