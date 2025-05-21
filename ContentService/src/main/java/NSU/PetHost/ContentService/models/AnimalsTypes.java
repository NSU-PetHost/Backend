package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "animal_types")
@Getter
@NoArgsConstructor
public class AnimalsTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    public AnimalsTypes(String name) {
        this.name = name;
    }

}
