package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointConfigRepository extends JpaRepository<PointConfigEntity, Long> {
    Optional<PointConfigEntity> findFirstBy();
}
