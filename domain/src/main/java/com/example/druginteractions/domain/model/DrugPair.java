package com.example.druginteractions.domain.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DrugPair(
    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugA,

    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]*[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugB
) {
    public DrugPair {
        // Normalize drug names: trim, convert to title case, and normalize spaces
        drugA = normalizeDrugName(drugA);
        drugB = normalizeDrugName(drugB);
    }

    private static String normalizeDrugName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Drug name cannot be null");
        }

        // Trim and normalize spaces
        String normalized = name.trim().replaceAll("\\s+", " ");

        // Convert to title case
        String[] words = normalized.split("\\s+|-");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                if (i > 0) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(words[i].charAt(0)))
                      .append(words[i].substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * Creates a normalized drug pair where drugs are always in alphabetical order
     */
    public static DrugPair of(String drugA, String drugB) {
        String normalizedA = normalizeDrugName(drugA);
        String normalizedB = normalizeDrugName(drugB);

        // Ensure consistent ordering
        if (normalizedA.compareToIgnoreCase(normalizedB) <= 0) {
            return new DrugPair(normalizedA, normalizedB);
        } else {
            return new DrugPair(normalizedB, normalizedA);
        }
    }
}
