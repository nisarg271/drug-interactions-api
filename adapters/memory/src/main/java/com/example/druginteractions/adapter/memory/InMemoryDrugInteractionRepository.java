package com.example.druginteractions.adapter.memory;

import com.example.druginteractions.domain.model.DrugInteraction;
import com.example.druginteractions.domain.port.DrugInteractionRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDrugInteractionRepository implements DrugInteractionRepository {
    private final Map<String, DrugInteraction> interactions = new ConcurrentHashMap<>();

    @Override
    public DrugInteraction save(DrugInteraction interaction) {
        String key = createKey(interaction.getDrugA(), interaction.getDrugB());
        interactions.put(key, interaction);
        return interaction;
    }

    @Override
    public Optional<DrugInteraction> findByDrugs(String drugA, String drugB) {
        String key = createKey(drugA, drugB);
        String reverseKey = createKey(drugB, drugA);
        return Optional.ofNullable(interactions.get(key))
                .or(() -> Optional.ofNullable(interactions.get(reverseKey)));
    }

    private String createKey(String drugA, String drugB) {
        return String.format("%s:%s", drugA.toLowerCase(), drugB.toLowerCase());
    }
}
