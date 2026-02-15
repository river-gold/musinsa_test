package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransactionEntity, Long> {
    List<PointTransactionEntity> findAllBySummaryIdIn(List<Long> summaryIds);
}
