package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class TestQueryContext implements QueryContext {

    private final SearchDao searchDao;

    public TestQueryContext(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }
}
