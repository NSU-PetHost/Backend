package NSU.PetHost.NotificationService.core.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "person_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSetting {

    @EmbeddedId
    private PersonSettingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private EventCategory eventCategory;

    @Type(JsonBinaryType.class)
    @Column(name = "channels", nullable = false, columnDefinition = "jsonb")
    private List<String> channels;

    public Long getPersonId() {
        return id != null ? id.getPersonId() : null;
    }

    public void setPersonId(Long personId) {
        if (id == null) {
            id = new PersonSettingId();
        }
        id.setPersonId(personId);
    }

    public Integer getCategoryId() {
        return id != null ? id.getCategoryId() : null;
    }

    public void setCategoryId(Integer categoryId) {
        if (id == null) {
            id = new PersonSettingId();
        }
        id.setCategoryId(categoryId);
    }
}