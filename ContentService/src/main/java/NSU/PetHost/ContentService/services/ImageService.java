package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.exceptions.images.DeleteImageException;
import NSU.PetHost.ContentService.exceptions.images.ImageNotFoundException;
import NSU.PetHost.ContentService.exceptions.images.SaveImageException;
import NSU.PetHost.ContentService.exceptions.InternalServerException;
import NSU.PetHost.ContentService.models.ImageResource;
import NSU.PetHost.ContentService.models.Images;
import NSU.PetHost.ContentService.repositories.ImageRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${upload.directory}")
    private String uploadDirectory;

    public Images uploadImage(MultipartFile file, boolean privacyImage) {
        try {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(uploadDirectory, newFileName);
            Files.copy(file.getInputStream(), path);

            Images image = new Images();
            image.setName(fileName);
            image.setFilePath(path.toString());
            image.setOwnerID(((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
            image.setPrivacy(privacyImage);

            imageRepository.save(image);
            return image;

        } catch (IOException e) {
            throw new SaveImageException("Failed to save image" + e.getMessage());
        }
    }

    public Images getImageByID(long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image not found"));
    }

    private boolean hasPermission(Images image) {
        if (!image.isPrivacy()) return true;

        PersonDetails currentUser = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Images> imagesFromUser = imageRepository.getImagesByOwnerID(currentUser.getId());

        return imagesFromUser.contains(image);
    }

    /**
     * Загружает картинку и оборачивает её в ресурс вместе с ContentType.
     * Проверяет, что текущий пользователь — владелец картинки.
     */
    public ImageResource loadImageAsResource(long imageId) {
        Images img = getImageByID(imageId);

        // получаем текущего пользователя
        long currentUserId = ((PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        // если не владелец — бросаем
        if (!hasPermission(img)) {
            throw new AccessDeniedException("Access denied");
        }

        // строим путь до файла
        Path file = Paths.get(uploadDirectory).resolve(img.getFilePath());
        if (!Files.exists(file)) {
            throw new ImageNotFoundException("Файл не найден на диске");
        }

        try {
            // определяем mime-типы по расширению
            String contentType = Files.probeContentType(file);
            Resource resource = new UrlResource(file.toUri());
            return new ImageResource(resource, contentType);
        } catch (IOException e) {
            throw new InternalServerException("Не удалось прочитать файл");
        }
    }

    public void deleteImageByPath(String path) {

        // строим путь до файла
        Path file = Paths.get(uploadDirectory).resolve(path);
        if (!Files.exists(file)) {
            throw new ImageNotFoundException("Файл не найден на диске");
        }

        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new DeleteImageException("Delete image failed");
        }

    }
}
