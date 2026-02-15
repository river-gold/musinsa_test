package musinsa.test.domain.point.transaction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointTransactionEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class PointTransaction {
    private Long id;
    @With
    private Long summaryId;
    private Long pointId;
    private PointStatus status;
    private BigDecimal beforeBalance;
    private BigDecimal amount;
    private BigDecimal balance;
    private Point point;

    static public PointTransaction of(PointTransactionEntity entity, Point point) {
        return PointTransaction.builder()
                .id(entity.getId())
                .summaryId(entity.getSummaryId())
                .pointId(entity.getPointId())
                .status(entity.getStatus())
                .beforeBalance(entity.getBeforeBalance())
                .amount(entity.getAmount())
                .balance(entity.getBalance())
                .point(point)
                .build();
    }

    static public List<PointTransaction> list(List<PointTransactionEntity> entities, List<Point> points) {
        Map<Long, Point> pointMap = points.stream().collect(Collectors.toMap(Point::id, p -> p));
        return entities.stream().map(entity -> of(entity, pointMap.get(entity.getPointId()))).toList();
    }

    public PointTransactionEntity createEntity(Long summaryId) {
        return PointTransactionEntity.builder()
                .summaryId(summaryId)
                .pointId(getPointId())
                .status(getStatus())
                .beforeBalance(getBeforeBalance())
                .amount(getAmount())
                .balance(getBalance())
                .build();
    }
}
