package musinsa.test.entity.point.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import musinsa.test.domain.point.code.PointStatus;

@Converter
public class PointStatusConverter implements AttributeConverter<PointStatus, Integer> {


    @Override
    public Integer convertToDatabaseColumn(PointStatus code) {
        if (code == null) return null;
        return code.getCode();
    }

    @Override
    public PointStatus convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        return PointStatus.of(code);
    }
}
