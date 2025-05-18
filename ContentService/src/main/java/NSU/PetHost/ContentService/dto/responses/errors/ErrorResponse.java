package NSU.PetHost.ContentService.dto.responses.errors;

import java.util.Map;

public record ErrorResponse(Map<String, Object> errors, long timestamp) {}