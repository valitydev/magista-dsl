package dev.vality.magista.dsl;

public class StatTestRequest {

    private String dsl;
    private String continuationToken;

    public StatTestRequest(String dsl) {
        this.dsl = dsl;
    }

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }
}
