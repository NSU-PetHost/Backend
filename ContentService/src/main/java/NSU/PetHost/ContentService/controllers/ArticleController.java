package NSU.PetHost.ContentService.controllers;

import NSU.PetHost.ContentService.dto.responses.positive.ArticleResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.models.Constants;
import NSU.PetHost.ContentService.services.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
@Validated
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> createArticle(@RequestParam(value = "title", defaultValue = "default title") @Size(max = 300) String title,
                                                    @RequestParam(value = "text", defaultValue = "default text") String text,
                                                    @RequestParam(value = "image") MultipartFile image) {
        return ResponseEntity
                .ok(articleService.createArticle(title, text, image));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> search(@RequestParam @NotBlank @Size(max = 300) @Schema(defaultValue = "default") String title,
                                                        @RequestParam @Min(0) @Schema(defaultValue = "0") int page) {
        return ResponseEntity
                .ok(articleService.searchByTitle(title, page, Constants.PAGE_SIZE));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ArticleResponse>> all(@RequestParam(defaultValue = "2025-05-19", required = true) LocalDate date,
                                                     @RequestParam @Schema(defaultValue = "0") @Min(0) int page) {
        return ResponseEntity
                .ok(articleService.getAllSortedByDateDesc(date, page, Constants.PAGE_SIZE));
    }

    @GetMapping("/by-author")
    public ResponseEntity<Page<ArticleResponse>> byAuthor(@RequestParam @Schema(defaultValue = "0") @Min(0) long authorId,
                                                          @RequestParam @Min(0) @Schema(defaultValue = "0") int page) {
        return ResponseEntity
                .ok(articleService.getByAuthorSorted(authorId, page, Constants.PAGE_SIZE));
    }

    @GetMapping("/getArticlesNeededApproved")
    @Operation(summary = "Admin access. Получение списка статей")
    public ResponseEntity<Page<ArticleResponse>> getAll(@RequestParam @Min(0) @Schema(defaultValue = "0") int page) {
        return ResponseEntity
                .ok(articleService.getAllArticlesNeededApproved(page, Constants.PAGE_SIZE));
    }

    @PutMapping("/changeArticlesChecked")
    @Operation(summary = "Admin access. Результат проверки статьи")
    public ResponseEntity<OkResponse> changeCheckArticle(@RequestParam @Min(0) long articleID,
                                                         @NotNull boolean approved,
                                                         @RequestParam(required = false) Long refusalReasonID) {
        return ResponseEntity
                .ok(articleService.changeApprovedArticle(articleID, approved, refusalReasonID));
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> updateArticle(@RequestParam @Min(1) long id,
                                                    @RequestParam(value = "title", required = false) @Size(max = 300) String title,
                                                    @RequestParam(value = "text", required = false) String text,
                                                    @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity
                .ok(articleService.updateArticle(id, title, text, image));
    }

    @PostMapping("/delete")
    public ResponseEntity<OkResponse> deleteArticle(@RequestParam @Min(1) long id) {
        return  ResponseEntity
                .ok(articleService.deleteById(id));
    }

}
