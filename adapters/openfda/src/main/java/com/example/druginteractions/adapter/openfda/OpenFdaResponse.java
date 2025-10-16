package com.example.druginteractions.adapter.openfda;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OpenFdaResponse {
    private List<Result> results;

    @Data
    public static class Result {
        @JsonProperty("term")
        private String term;

        @JsonProperty("count")
        private int count;
    }
}
