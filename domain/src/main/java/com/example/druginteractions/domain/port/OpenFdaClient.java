package com.example.druginteractions.domain.port;

import com.example.druginteractions.domain.model.OpenFdaSignal;
import reactor.core.publisher.Mono;

public interface OpenFdaClient {
    /**
     * Fetch adverse event signals for a drug pair from OpenFDA
     * @param drugA First drug name (will be normalized)
     * @param drugB Second drug name (will be normalized)
     * @param limit Maximum number of top reactions to return
     * @return Mono containing the signal analysis
     */
    Mono<OpenFdaSignal> fetchSignals(String drugA, String drugB, int limit);
}
