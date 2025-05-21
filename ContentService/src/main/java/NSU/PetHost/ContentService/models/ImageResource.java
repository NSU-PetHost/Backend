package NSU.PetHost.ContentService.models;

import lombok.*;
import org.springframework.core.io.Resource;

public record ImageResource(Resource resource, String contentType) {}
