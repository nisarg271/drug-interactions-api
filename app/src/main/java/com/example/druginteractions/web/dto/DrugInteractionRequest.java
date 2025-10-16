package com.example.druginteractions.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DrugInteractionRequest(
    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugA,

    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugB,

    @Size(min = 1, message = "Note cannot be empty")
    String note
) {}
