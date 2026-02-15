package musinsa.test.entity.point.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import musinsa.test.domain.point.code.PointReference;

@Converter
public class PointReferenceConverter implements AttributeConverter<PointReference, Integer> {


    @Override
    public Integer convertToDatabaseColumn(PointReference code) {
        if (code == null) return null;
        return code.getCode();
    }

    @Override
    public PointReference convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        return PointReference.of(code);
    }
}
