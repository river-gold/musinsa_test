package musinsa.test.domain.point;

import musinsa.test.entity.point.PointEntity;
import musinsa.test.entity.point.repo.PointQuerydslRepository;
import musinsa.test.entity.point.repo.PointRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointQuerydslRepository pointQuerydslRepository;

    @Mock
    private PointKeyGenerator pointKeyGenerator;

    @InjectMocks
    @Spy
    private PointService pointService;

    @Test
    void findBalanceTest() {
        PointEntity entity = Instancio.create(PointEntity.class);
        given(pointQuerydslRepository.findByKeyWithLock("test"))
                .willReturn(entity);

        Optional<Point> expected = Optional.of(Point.of(entity));
        Optional<Point> actual = pointService.getPointByKeyWithLock("test");

        assertEquals(expected, actual);
    }

    @Test
    void getPointsByUserWithTest() {
        List<PointEntity> entities = List.of(
                Instancio.create(PointEntity.class),
                Instancio.create(PointEntity.class)
        );
        given(pointQuerydslRepository.findAllByUserWithLock(123L))
                .willReturn(entities);

        List<Point> expected = entities.stream().map(Point::of).toList();
        List<Point> actual = pointService.getPointsByUserWithLock(123L);

        assertEquals(expected, actual);
    }

    @Test
    void getPointsTest() {
        List<PointEntity> entities = List.of(
                Instancio.create(PointEntity.class),
                Instancio.create(PointEntity.class)
        );
        given(pointQuerydslRepository.findAll(List.of(1L, 2L, 3L)))
                .willReturn(entities);

        List<Point> expected = entities.stream().map(Point::of).toList();
        List<Point> actual = pointService.getPoints(List.of(1L, 2L, 3L));

        assertEquals(expected, actual);
    }

    @Test
    void saveTest() {
        {
            // id가 없으면 point 생성
            Point point = Instancio.of(Point.class).ignore(field(Point::id)).create();

            doReturn(point).when(pointService).create(point);

            Point actual = pointService.save(point);

            assertEquals(point, actual);

            verify(pointService, times(1)).create(point);
        }
        {
            // id가 있으면 point 수정
            Point point = Instancio.of(Point.class).create();

            doReturn(point).when(pointService).update(point);

            Point actual = pointService.save(point);

            assertEquals(point, actual);

            verify(pointService, times(1)).update(point);
        }
    }

    @Test
    void createTest() {
        Point point = Instancio.create(Point.class);
        PointEntity entity = Instancio.create(PointEntity.class);

        given(pointKeyGenerator.generate()).willReturn("1");
        given(pointRepository.save(point.createEntity("1")))
                .willReturn(entity);

        Point expected = Point.of(entity);
        Point actual = pointService.create(point);

        assertEquals(expected, actual);
    }

    @Test
    void updateAllTest() {
        List<Point> points = List.of(
                Instancio.create(Point.class),
                Instancio.create(Point.class)
        );
        List<PointEntity> entities = List.of(
                Instancio.create(PointEntity.class),
                Instancio.create(PointEntity.class)
        );

        given(pointRepository.saveAll(points.stream().map(Point::toEntity).toList()))
                .willReturn(entities);

        List<Point> expected = entities.stream().map(Point::of).toList();
        List<Point> actual = pointService.updateAll(points);

        assertEquals(expected, actual);
    }

    @Test
    void updateTest() {
        Point point = Instancio.create(Point.class);
        PointEntity entity = Instancio.create(PointEntity.class);

        given(pointRepository.save(point.toEntity()))
                .willReturn(entity);

        Point expected = Point.of(entity);
        Point actual = pointService.update(point);

        assertEquals(expected, actual);
    }
}
