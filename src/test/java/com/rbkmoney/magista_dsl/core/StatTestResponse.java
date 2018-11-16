package com.rbkmoney.magista_dsl.core;

import java.util.List;

public class StatTestResponse {

    private StatTestResponseData statTestResponseData;
    private String continuationToken;

    public StatTestResponse(List<Dummy> statResponseData) {
        this.statTestResponseData = new StatTestResponseData(statResponseData);
    }

    public StatTestResponseData getStatTestResponseData() {
        return statTestResponseData;
    }

    public void setStatTestResponseData(StatTestResponseData statTestResponseData) {
        this.statTestResponseData = statTestResponseData;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }
}
