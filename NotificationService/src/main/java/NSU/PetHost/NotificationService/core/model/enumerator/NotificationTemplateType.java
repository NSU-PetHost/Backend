package NSU.PetHost.NotificationService.core.model.enumerator;

import lombok.Getter;

@Getter
public enum NotificationTemplateType {
    PERSON_DEFINED_REMINDER("PERSON_DEFINED_REMINDER"),
    SYSTEM_EVENT_GENERAL("SYSTEM_EVENT_GENERAL"),
    SYSTEM_EVENT_HOLIDAY("SYSTEM_EVENT_HOLIDAY"),
    SYSTEM_EVENT_PROMOTIONAL("SYSTEM_EVENT_PROMOTIONAL");

    private String value;

    private NotificationTemplateType(String value) {
        this.value = value;
    }

    public static NotificationTemplateType fromValue(String dbValue) {
        for (NotificationTemplateType type : values()) {
            if (type.value.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type value: " + dbValue);
    }
}