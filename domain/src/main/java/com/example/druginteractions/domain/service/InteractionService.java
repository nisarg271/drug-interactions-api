package com.example.druginteractions.domain.service;

import com.example.druginteractions.domain.model.DrugPair;
import com.example.druginteractions.domain.model.InteractionNote;
import com.example.druginteractions.domain.model.OpenFdaSignal;
import com.example.druginteractions.domain.port.InteractionRepository;
import com.example.druginteractions.domain.port.OpenFdaClient;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class InteractionService {
    private final InteractionRepository repository;
    private final OpenFdaClient openFdaClient;
    private final Validator validator;

    /**
     * Find an interaction note for two drugs
     * @param drugA First drug name
     * @param drugB Second drug name
     * @return Optional containing the interaction note if found
     */
    public Optional<InteractionNote> findInteraction(String drugA, String drugB) {
        DrugPair pair = DrugPair.of(drugA, drugB);
        validateDrugPair(pair);
        return repository.find(pair);
    }

    /**
     * Create or update an interaction note
     * @param drugA First drug name
     * @param drugB Second drug name
     * @param note The interaction note text
     * @return The saved interaction note
     */
    public InteractionNote upsertInteraction(String drugA, String drugB, String note) {
        DrugPair pair = DrugPair.of(drugA, drugB);
        validateDrugPair(pair);

        return repository.find(pair)
            .map(existing -> existing.withUpdatedNote(note))
            .map(repository::upsert)
            .orElseGet(() -> repository.upsert(InteractionNote.create(pair, note)));
    }

    /**
     * Fetch adverse event signals for a drug pair
     * @param drugA First drug name
     * @param drugB Second drug name
     * @param limit Maximum number of top reactions to return
     * @return Mono containing the signal analysis
     */
    public Mono<OpenFdaSignal> getSignals(String drugA, String drugB, int limit) {
        DrugPair pair = DrugPair.of(drugA, drugB);
        validateDrugPair(pair);

        if (limit < 1 || limit > 50) {
            throw new IllegalArgumentException("Limit must be between 1 and 50");
        }

        return openFdaClient.fetchSignals(pair.drugA(), pair.drugB(), limit);
    }

    private void validateDrugPair(@Valid DrugPair pair) {
        var violations = validator.validate(pair);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Invalid drug pair")
            );
        }
    }
}
