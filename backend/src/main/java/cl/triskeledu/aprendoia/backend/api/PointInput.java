package cl.triskeledu.aprendoia.backend.api;

import jakarta.validation.constraints.*;

public record PointInput(
    @NotBlank @Size(max=120) String title,
    @NotBlank @Size(max=3000) String description,
    @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
    @NotBlank @Size(max=200) String qrPayload,
    @Min(1) @Max(100) int points
) {}
