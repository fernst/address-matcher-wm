package com.fvelasquez.wm.domain;

import java.util.List;

public class GeocodeAPIResponse {

    String status;
    List<Result> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
