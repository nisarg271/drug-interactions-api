package com.example.druginteractions.web;

import com.example.druginteractions.domain.service.InteractionService;
import com.example.druginteractions.web.dto.DrugInteractionRequest;
import com.example.druginteractions.web.dto.DrugInteractionResponse;
import com.example.druginteractions.web.dto.DrugSignalResponse;
import com.example.druginteractions.web.mapper.DrugInteractionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
public class DrugInteractionController {
    private final InteractionService service;
    private final DrugInteractionMapper mapper;

    @PostMapping("/interactions")
    public ResponseEntity<DrugInteractionResponse> createInteraction(
            @Valid @RequestBody DrugInteractionRequest request) {
        var note = service.upsertInteraction(
            request.drugA(),
            request.drugB(),
            request.note()
        );
        return ResponseEntity.ok(mapper.toResponse(note));
    }

    @GetMapping("/interactions")
    public ResponseEntity<DrugInteractionResponse> getInteraction(
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$") String drugA,
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$") String drugB) {
        return service.findInteraction(drugA, drugB)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/signals")
    public Mono<ResponseEntity<DrugSignalResponse>> getSignals(
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$") String drugA,
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$") String drugB,
            @RequestParam(defaultValue = "50") @Min(1) @Max(50) int limit) {
        return service.getSignals(drugA, drugB, limit)
                .map(mapper::toSignalResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
