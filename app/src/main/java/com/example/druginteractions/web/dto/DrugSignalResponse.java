package com.example.druginteractions.web.dto;

import java.util.List;

public record DrugSignalResponse(
    long count,
    List<ReactionCount> topReactions
) {
    public record ReactionCount(
        String reaction,
        long count
    ) {}
}
