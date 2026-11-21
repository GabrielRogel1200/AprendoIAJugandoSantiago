package cl.triskeledu.aprendoia.backend.api;

public record ScanResponse(Long pointId, String title, int earnedPoints, boolean firstScan, String message) {}
