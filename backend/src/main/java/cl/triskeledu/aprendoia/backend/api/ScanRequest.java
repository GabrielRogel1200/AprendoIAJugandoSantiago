package cl.triskeledu.aprendoia.backend.api;

import jakarta.validation.constraints.*;
public record ScanRequest(@NotNull @Positive Long playerId, @NotBlank String qrPayload) {}
