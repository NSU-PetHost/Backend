package NSU.PetHost.ContentService.models;

import lombok.Getter;

@Getter
public enum StatusType {
    WAITING_REVIEW("WAITING_REVIEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String dbValue;

    StatusType(String dbValue) {
        this.dbValue = dbValue;
    }

    // Преобразует строку из БД в enum
    public static StatusType fromDbValue(String dbValue) {
        for (StatusType status : StatusType.values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + dbValue);
    }

    // Преобразует строку из enum в БД
    public static String toDbValue(StatusType status) {
        for (StatusType curStatus : StatusType.values()) {
            if (status.equals(curStatus)) {
                return status.toString();
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
