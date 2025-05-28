package NSU.PetHost.NotificationService.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonSettingId implements Serializable {
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
}