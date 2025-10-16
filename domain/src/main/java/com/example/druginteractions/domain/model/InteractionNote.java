package com.example.druginteractions.domain.model;

import java.time.Instant;
import java.util.UUID;

public record InteractionNote(
    UUID id,
    DrugPair pair,
    String note,
    Instant updatedAt
) {
    public InteractionNote {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        if (pair == null) {
            throw new IllegalArgumentException("Drug pair cannot be null");
        }
    }

    public static InteractionNote create(DrugPair pair, String note) {
        return new InteractionNote(UUID.randomUUID(), pair, note, Instant.now());
    }

    public InteractionNote withUpdatedNote(String newNote) {
        return new InteractionNote(this.id, this.pair, newNote, Instant.now());
    }
}
