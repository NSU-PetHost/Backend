package NSU.PetHost.ContentService.models;// Statistics.java

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    private Animals animal;

    @Column(nullable = false)
    private int appetite = 0;

    @Column(nullable = false)
    private int thirst = 0;

    @Column(nullable = false)
    private int activity = 0;

    @Column(name = "gastrointestinal_tract", nullable = false)
    private int gastrointestinalTract = 0;

    private String note;
}
