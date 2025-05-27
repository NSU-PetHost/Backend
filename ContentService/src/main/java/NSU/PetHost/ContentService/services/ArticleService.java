package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.responses.positive.ArticleResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.exceptions.articles.ArticlesNotFoundException;
import NSU.PetHost.ContentService.models.Articles;
import NSU.PetHost.ContentService.models.StatusType;
import NSU.PetHost.ContentService.repositories.ArticleRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Validated
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final RefusalReasonsService refusalReasonsService;
    private final ImageService imageService;

    public Articles getArticlesById(long id) {
        return articleRepository.findById(id).orElseThrow(() -> new ArticlesNotFoundException("Article Not Found"));
    }

    private boolean hasPermission(Articles articles) {
        return articles.getOwnerID() == ((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public OkResponse createArticle(@Size(max = 300) String title, String text, MultipartFile image) {

        Articles article = new Articles();

        article.setTitle(title);
        article.setText(text);
        article.setOwnerID(((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        article.setImage(imageService.uploadImage(image, false));
        articleRepository.save(article);

        return new OkResponse("Article created", System.currentTimeMillis());
    }

    public Page<ArticleResponse> getByAuthorSorted(long authorId, int page, int size) {

        Sort sortObj = Sort.by(
                Sort.Order.by("createdAt").with(Sort.Direction.DESC)
        );

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Articles> articles = articleRepository.findAllByOwnerIDAndStatusIs(authorId, StatusType.toDbValue(StatusType.APPROVED), pageable);

        return articles.map(this::convertArticleToArticleResponse);

    }

    @Cacheable(
            value = "articlesFilteredInfo",
            key = "'articlesFilteredByDate' + #page + '-' + #size"
    )
    public Page<ArticleResponse> getAllSortedByDateDesc(LocalDate date,
                                                        @Min(0) int page,
                                                        @Min(0) int size) {

        Sort sortObj = Sort.by(
                Sort.Order.by("createdAt").with(Sort.Direction.DESC)
        );

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Articles> articles;

        if (date != null) {
            articles = articleRepository.findAllByCreatedAtIsAfterAndStatusIs(date, StatusType.toDbValue(StatusType.APPROVED), pageable);
        } else {
            articles = articleRepository.findAllByCreatedAtIsAfterAndStatusIs(LocalDate.now(), StatusType.toDbValue(StatusType.APPROVED), PageRequest.of(page, size, sortObj));
        }

        return articles.map(this::convertArticleToArticleResponse);

    }

    public Page<ArticleResponse> searchByTitle(@NotBlank @Size(max = 300) String title,
                                               @Min(0) int page,
                                               @Min(0) int size) {

        Sort sortObj = Sort.by(
                Sort.Order.by("createdAt").with(Sort.Direction.DESC)
        );

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Articles> articles = articleRepository.findAllByTitleContainingAndStatusIs(title, StatusType.toDbValue(StatusType.APPROVED), pageable);

        return articles.map(this::convertArticleToArticleResponse);

    }

    private ArticleResponse convertArticleToArticleResponse(Articles articles) {
        return new ArticleResponse(
                articles.getId(),
                articles.getTitle(),
                articles.getText(),
                articles.getImage().getId(),
                articles.getCreatedAt(),
                articles.getOwnerID()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public Page<ArticleResponse> getAllArticlesNeededApproved(@Min(0) int page,
                                                              @Min(0) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<Articles> articles = articleRepository.findAllByStatusIs(StatusType.toDbValue(StatusType.WAITING_REVIEW), pageable);

        return articles.map(this::convertArticleToArticleResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse changeApprovedArticle(@Min(0) long articleId, @NotNull boolean approved, Long refusalReasonID) {

        Articles articles = getArticlesById(articleId);

        if (approved) {
            articles.setStatus(StatusType.toDbValue(StatusType.APPROVED));
        } else {
            articles.setStatus(StatusType.toDbValue(StatusType.REJECTED));
            articles.setRefusalReasons(refusalReasonsService.getRefusalReasonsByID(refusalReasonID));
        }

        articles.setModeratorID(((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        articleRepository.save(articles);

        return new OkResponse("Article updated", System.currentTimeMillis());
    }

    public OkResponse updateArticle(@Min(1) long id, @Size(max = 300) String title, String text, MultipartFile image) {

        Articles articles = getArticlesById(id);
        boolean changed = false;

        if (!hasPermission(articles)) throw new AccessDeniedException("Not allowed to update this article");

        if (title != null && !title.trim().isEmpty()) {
            articles.setTitle(title);
            changed = true;
        }
        ;
        if (text != null && !text.trim().isEmpty()) {
            articles.setText(text);
            changed = true;
        }
        if (image != null) {
            articles.setImage(imageService.uploadImage(image, false));
            changed = true;
        }
        if (changed) {
            articles.setStatus(StatusType.toDbValue(StatusType.WAITING_REVIEW));
        }
        articleRepository.save(articles);

        return new OkResponse("Article updated. Now in status waiting review", System.currentTimeMillis());

    }

    public OkResponse deleteById(@Min(1) long id) {
        articleRepository.deleteById(id);
        return new OkResponse("Article deleted", System.currentTimeMillis());
    }
}
