package com.fvelasquez.wm.domain;

public enum Status {

    FOUND("FOUND"),
    NOT_FOUND("NOT_FOUND");

    String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
