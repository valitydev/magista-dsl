package com.rbkmoney.magista_dsl.core;

public class Dummy {
    private Long id;
    private String kek;
    private String fromTime;
    private String toTime;

    public Dummy(Long id, String kek, String fromTime, String toTime) {
        this.id = id;
        this.kek = kek;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKek() {
        return kek;
    }

    public void setKek(String kek) {
        this.kek = kek;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
}
