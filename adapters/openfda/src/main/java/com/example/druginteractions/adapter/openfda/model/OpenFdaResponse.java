package com.example.druginteractions.adapter.openfda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OpenFdaResponse {
    private Meta meta;
    private List<Result> results;

    @Data
    public static class Meta {
        private long totalCount;
    }

    @Data
    public static class Result {
        @JsonProperty("term")
        private String reaction;

        @JsonProperty("count")
        private long count;
    }
}
