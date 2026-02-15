package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<PointEntity, Long> {
}
