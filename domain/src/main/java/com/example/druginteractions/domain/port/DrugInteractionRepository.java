package com.example.druginteractions.domain.port;

import com.example.druginteractions.domain.model.DrugInteraction;
import java.util.Optional;

public interface DrugInteractionRepository {
    DrugInteraction save(DrugInteraction interaction);
    Optional<DrugInteraction> findByDrugs(String drugA, String drugB);
}
