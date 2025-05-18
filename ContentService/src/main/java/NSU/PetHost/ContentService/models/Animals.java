package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

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

    private Date dateOfBirth;

    private double weight;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Images image;
}
