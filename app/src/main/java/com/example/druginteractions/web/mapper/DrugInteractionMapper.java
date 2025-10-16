package com.example.druginteractions.web.mapper;

import com.example.druginteractions.domain.model.DrugPair;
import com.example.druginteractions.domain.model.InteractionNote;
import com.example.druginteractions.domain.model.OpenFdaSignal;
import com.example.druginteractions.web.dto.DrugInteractionRequest;
import com.example.druginteractions.web.dto.DrugInteractionResponse;
import com.example.druginteractions.web.dto.DrugSignalResponse;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.stream.Collectors;

@Component
public class DrugInteractionMapper {

    public DrugInteractionResponse toResponse(InteractionNote note) {
        return new DrugInteractionResponse(
            note.id(),
            note.pair().drugA(),
            note.pair().drugB(),
            note.note(),
            note.updatedAt()
        );
    }

    public DrugPair toDrugPair(String drugA, String drugB) {
        return DrugPair.of(drugA, drugB);
    }

    public InteractionNote toInteractionNote(DrugInteractionRequest request) {
        return InteractionNote.create(
            toDrugPair(request.drugA(), request.drugB()),
            request.note()
        );
    }

    public DrugSignalResponse toSignalResponse(OpenFdaSignal signal) {
        return new DrugSignalResponse(
            signal.count(),
            signal.topReactions().stream()
                .map(entry -> new DrugSignalResponse.ReactionCount(
                    entry.getKey(),
                    entry.getValue()
                ))
                .collect(Collectors.toList())
        );
    }
}
