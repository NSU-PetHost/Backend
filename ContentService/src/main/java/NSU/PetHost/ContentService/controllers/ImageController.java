package NSU.PetHost.ContentService.controllers;

import NSU.PetHost.ContentService.models.ImageResource;
import NSU.PetHost.ContentService.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> getImage(@PathVariable long id) {

        ImageResource image = imageService.loadImageAsResource(id);

        MediaType mediaType = MediaType.parseMediaType(image.contentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(image.resource());
    }
}