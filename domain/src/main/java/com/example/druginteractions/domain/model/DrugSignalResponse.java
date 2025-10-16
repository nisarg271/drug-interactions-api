package com.example.druginteractions.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DrugSignalResponse {
    int count;
    List<Reaction> topReactions;

    @Value
    @Builder
    public static class Reaction {
        String reaction;
        int count;
    }
}
