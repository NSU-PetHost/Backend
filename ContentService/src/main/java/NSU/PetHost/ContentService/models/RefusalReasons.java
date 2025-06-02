package NSU.PetHost.ContentService.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refusal_reasons")
@Data
@NoArgsConstructor
public class RefusalReasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(length = 70)
    private String reason;

    public RefusalReasons(String reason) {
        this.reason = reason;
    }

}