package com.example.druginteractions.domain.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DrugInteraction {
    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$")
    String drugA;

    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$")
    String drugB;

    String note;
}
