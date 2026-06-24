package com.inventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull @Min(0) @Max(1000) Integer quantity,
        @Size(max = 255) String image,
        @NotNull Integer categoryId) {
}
