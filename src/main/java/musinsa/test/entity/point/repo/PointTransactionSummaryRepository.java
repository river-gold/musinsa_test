package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointTransactionSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionSummaryRepository extends JpaRepository<PointTransactionSummaryEntity, Long> {
}
