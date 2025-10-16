package com.example.druginteractions.domain.service;

import com.example.druginteractions.domain.model.DrugInteraction;
import com.example.druginteractions.domain.model.OpenFdaSignal;
import com.example.druginteractions.domain.port.DrugInteractionRepository;
import com.example.druginteractions.domain.port.OpenFdaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrugInteractionService {
    private final DrugInteractionRepository repository;
    private final OpenFdaClient openFdaClient;

    public DrugInteraction upsertInteraction(DrugInteraction interaction) {
        return repository.save(interaction);
    }

    public Optional<DrugInteraction> findInteraction(String drugA, String drugB) {
        return repository.findByDrugs(drugA, drugB);
    }

    public Mono<OpenFdaSignal> getSignals(String drugA, String drugB, int limit) {
        return openFdaClient.fetchSignals(drugA, drugB, limit);
    }
}
