package dev.vality.magista.dsl;

public class TestQueryContext implements QueryContext {

    private final SearchDao searchDao;

    public TestQueryContext(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }
}
