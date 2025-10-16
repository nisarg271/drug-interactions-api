package com.example.druginteractions.domain.port;

import com.example.druginteractions.domain.model.DrugPair;
import com.example.druginteractions.domain.model.InteractionNote;
import java.util.Optional;

public interface InteractionRepository {
    /**
     * Find an interaction note for a drug pair
     * @param pair The normalized drug pair to search for
     * @return Optional containing the interaction note if found
     */
    Optional<InteractionNote> find(DrugPair pair);

    /**
     * Create or update an interaction note
     * @param note The note to upsert
     * @return The saved interaction note with updated metadata
     */
    InteractionNote upsert(InteractionNote note);
}
