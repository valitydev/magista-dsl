package com.rbkmoney.magista.dsl;

import java.util.List;

public class StatTestResponseData {
    private List<Dummy> dummies;

    public List<Dummy> getDummies() {
        return dummies;
    }

    public void setDummies(List<Dummy> dummies) {
        this.dummies = dummies;
    }

    public StatTestResponseData(List<Dummy> dummies) {
        this.dummies = dummies;
    }
}
