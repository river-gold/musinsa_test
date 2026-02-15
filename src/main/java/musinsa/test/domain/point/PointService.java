package musinsa.test.domain.point;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import musinsa.test.entity.point.PointEntity;
import musinsa.test.entity.point.repo.PointQuerydslRepository;
import musinsa.test.entity.point.repo.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final PointQuerydslRepository pointQuerydslRepository;
    private final PointKeyGenerator pointKeyGenerator;

    public Optional<Point> getPointByKeyWithLock(String pointKey) {
        return Optional
                .ofNullable(pointQuerydslRepository.findByKeyWithLock(pointKey))
                .map(Point::of);
    }

    public List<Point> getPointsByUserWithLock(Long userId) {
        return pointQuerydslRepository.findAllByUserWithLock(userId)
                .stream()
                .map(Point::of)
                .toList();
    }

    public List<Point> getPoints(List<Long> ids) {
        return pointQuerydslRepository.findAll(ids)
                .stream()
                .map(Point::of)
                .toList();
    }

    public Point save(Point point) {
        if (point.id() == null) {
            return create(point);
        } else {
            return update(point);
        }
    }

    public Point create(Point point) {
        PointEntity entity = point.createEntity(pointKeyGenerator.generate());
        return Point.of(pointRepository.save(entity));
    }

    @Transactional
    public List<Point> updateAll(List<Point> points) {
        return pointRepository
                .saveAll(points.stream().map(Point::toEntity).toList())
                .stream().map(Point::of)
                .toList();
    }

    public Point update(Point point) {
        return Point.of(pointRepository.save(point.toEntity()));
    }
}
