package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Images image;

    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(nullable = false)
    private long ownerID;

    @Column(nullable = true)
    private Long moderatorID = null;

    @Column(nullable = false)
    private String status = StatusType.toDbValue(StatusType.WAITING_REVIEW);

    @ManyToOne
    @JoinColumn(nullable = true, name = "refusal_reasons", referencedColumnName = "id")
    private RefusalReasons refusalReasons = null;

}
