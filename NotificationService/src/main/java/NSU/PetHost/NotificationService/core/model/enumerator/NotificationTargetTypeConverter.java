package NSU.PetHost.NotificationService.core.model.enumerator;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;


@Converter(autoApply = true)
public class NotificationTargetTypeConverter implements AttributeConverter<NotificationTargetType, String> {
    @Override
    public String convertToDatabaseColumn(NotificationTargetType targetType) {
        return targetType != null ? targetType.getValue() : null;
    }

    @Override
    public NotificationTargetType convertToEntityAttribute(String dbData) {
        return dbData != null ? NotificationTargetType.fromValue(dbData) : null;
    }
}