package musinsa.test.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Function;

public class BigDecimalUtils {
    static public <T> BigDecimal sum(Collection<T> collection, Function<T, BigDecimal> mapper) {
        return collection.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
