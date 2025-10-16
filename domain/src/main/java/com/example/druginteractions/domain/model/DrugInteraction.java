package com.example.druginteractions.domain.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Builder
@Getter
@Setter
public class DrugInteraction {
    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$")
    public String drugA;

    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$")
    public String drugB;

    public String note;
}
