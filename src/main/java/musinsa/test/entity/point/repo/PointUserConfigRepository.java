package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointUserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointUserConfigRepository extends JpaRepository<PointUserConfigEntity, Long> {

    Optional<PointUserConfigEntity> findByUserId(Long userId);
}
