package musinsa.test.entity.point.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;
import musinsa.test.domain.point.code.PointEarnType;

@Log4j2
@Converter
public class PointEarnTypeConverter implements AttributeConverter<PointEarnType, Integer> {


    @Override
    public Integer convertToDatabaseColumn(PointEarnType code) {
        if (code == null) return null;
        return code.getCode();
    }

    @Override
    public PointEarnType convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        return PointEarnType.of(code);
    }
}
