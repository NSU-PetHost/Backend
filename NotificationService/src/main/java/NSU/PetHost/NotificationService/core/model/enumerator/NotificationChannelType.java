package NSU.PetHost.NotificationService.core.model.enumerator;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Каналы отправки уведомлений")
public enum NotificationChannelType {
    EMAIL("email"),
    PUSH("push"),
    ON_SITE("on_site");

    private final String dbValue;

    NotificationChannelType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static NotificationChannelType fromDbValue(String dbValue) {
        for (NotificationChannelType type : values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown channel value: " + dbValue);
    }
}