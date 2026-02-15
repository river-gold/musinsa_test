package musinsa.test.domain.point.transaction;

import musinsa.test.domain.point.Point;
import musinsa.test.entity.point.PointTransactionEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointTransactionTest {

    @Test
    void listTest() {
        List<PointTransactionEntity> entities = List.of(
                Instancio.of(PointTransactionEntity.class).set(field(PointTransactionEntity::getPointId), 1L).create(),
                Instancio.of(PointTransactionEntity.class).set(field(PointTransactionEntity::getPointId), 1L).create(),
                Instancio.of(PointTransactionEntity.class).set(field(PointTransactionEntity::getPointId), 2L).create(),
                Instancio.of(PointTransactionEntity.class).set(field(PointTransactionEntity::getPointId), 2L).create(),
                Instancio.of(PointTransactionEntity.class).set(field(PointTransactionEntity::getPointId), 3L).create()
        );
        List<Point> points = List.of(
                Instancio.of(Point.class).set(field(Point::id), 1L).create(),
                Instancio.of(Point.class).set(field(Point::id), 2L).create(),
                Instancio.of(Point.class).set(field(Point::id), 3L).create(),
                Instancio.of(Point.class).set(field(Point::id), 4L).create()
        );

        List<PointTransaction> expected = List.of(
                PointTransaction.of(entities.get(0), points.get(0)),
                PointTransaction.of(entities.get(1), points.get(0)),
                PointTransaction.of(entities.get(2), points.get(1)),
                PointTransaction.of(entities.get(3), points.get(1)),
                PointTransaction.of(entities.get(4), points.get(2))
        );
        List<PointTransaction> actual = PointTransaction.list(entities, points);

        assertEquals(expected, actual);
    }
}
