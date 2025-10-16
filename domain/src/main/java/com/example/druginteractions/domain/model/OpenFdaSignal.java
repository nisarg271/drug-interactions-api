package com.example.druginteractions.domain.model;

import java.util.List;
import java.util.Map;

public record OpenFdaSignal(
    long count,
    List<Map.Entry<String, Long>> topReactions
) {
    public OpenFdaSignal {
        if (topReactions == null) {
            throw new IllegalArgumentException("Top reactions cannot be null");
        }
    }
}
