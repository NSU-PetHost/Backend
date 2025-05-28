package NSU.PetHost.NotificationService.core.model.enumerator;

import lombok.Getter;

@Getter
public enum NotificationTargetType {
    SINGLE_USER("SINGLE_USER"),
    ALL_USERS("ALL_USERS");

    private String value;
    NotificationTargetType(String value) {
        this.value = value;
    }

    public static NotificationTargetType fromValue(String dbValue) {
        for (NotificationTargetType type : values()) {
            if (type.value.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type value: " + dbValue);
    }
}