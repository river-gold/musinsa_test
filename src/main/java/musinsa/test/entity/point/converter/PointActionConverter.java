package musinsa.test.entity.point.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import musinsa.test.domain.point.code.PointAction;

@Converter
public class PointActionConverter implements AttributeConverter<PointAction, Integer> {


    @Override
    public Integer convertToDatabaseColumn(PointAction code) {
        if (code == null) return null;
        return code.getCode();
    }

    @Override
    public PointAction convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        return PointAction.of(code);
    }
}
