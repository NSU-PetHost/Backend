package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "approved_articles")
@Data
public class ApprovedArticles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @OneToOne
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Articles article;

    @Column(nullable = false)
    private long moderatorId;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

