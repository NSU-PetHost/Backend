package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class Animals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    private AnimalsTypes animalsType;

    private long ownerId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private double weight;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Images image;
}
