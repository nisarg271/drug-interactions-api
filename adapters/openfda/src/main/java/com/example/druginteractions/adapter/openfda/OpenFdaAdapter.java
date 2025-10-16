package com.example.druginteractions.adapter.openfda;

import com.example.druginteractions.adapter.openfda.model.OpenFdaResponse;
import com.example.druginteractions.domain.model.OpenFdaSignal;
import com.example.druginteractions.domain.port.OpenFdaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpenFdaAdapter implements OpenFdaClient {
    private final WebClient openFdaWebClient;

    @Override
    public Mono<OpenFdaSignal> fetchSignals(String drugA, String drugB, int limit) {
        String query = buildDrugQuery(drugA, drugB);
        String url = buildUrl(query, limit);

        return openFdaWebClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(OpenFdaResponse.class)
            .map(this::mapToSignal)
            .onErrorMap(this::handleError);
    }

    private String buildDrugQuery(String drugA, String drugB) {
        // Case-insensitive search for both drugs in medicinal product field
        return String.format(
            "patient.drug.medicinalproduct:\"%s\" AND patient.drug.medicinalproduct:\"%s\"",
            drugA.toLowerCase(),
            drugB.toLowerCase()
        );
    }

    private String buildUrl(String query, int limit) {
        return UriComponentsBuilder.fromPath("/drug/event.json")
            .queryParam("search", query)
            .queryParam("count", "patient.reaction.reactionmeddrapt.exact")
            .queryParam("limit", limit)
            .build()
            .toString();
    }

    private OpenFdaSignal mapToSignal(OpenFdaResponse response) {
        if (response == null || response.getResults() == null) {
            return new OpenFdaSignal(0, List.of());
        }

        long totalCount = response.getMeta() != null ? response.getMeta().getTotalCount() : 0;
        List<Map.Entry<String, Long>> topReactions = response.getResults().stream()
            .map(result -> new AbstractMap.SimpleEntry<>(
                result.getReaction(),
                result.getCount()
            ))
            .collect(Collectors.toList());

        return new OpenFdaSignal(totalCount, topReactions);
    }

    private Throwable handleError(Throwable error) {
        if (error instanceof WebClientResponseException wcre) {
            HttpStatusCode status = wcre.getStatusCode();
            if (status.is4xxClientError()) {
                if (status.value() == 429) {  // Rate limit exceeded
                    return new OpenFdaRateLimitException("Rate limit exceeded", wcre);
                }
                return new IllegalArgumentException("Invalid request to OpenFDA API", wcre);
            }
        }
        return new OpenFdaException("Error fetching data from OpenFDA", error);
    }
}
