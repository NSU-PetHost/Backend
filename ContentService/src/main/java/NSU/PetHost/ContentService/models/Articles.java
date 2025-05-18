package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "articles")
@Data
public class Articles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String imageLink;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private long ownerID;

    @Column(nullable = false)
    private boolean approved = false;

}
