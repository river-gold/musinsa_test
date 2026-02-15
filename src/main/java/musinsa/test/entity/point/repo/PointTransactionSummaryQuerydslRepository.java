package musinsa.test.entity.point.repo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import musinsa.test.entity.point.QPointTransactionSummaryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointTransactionSummaryQuerydslRepository {
    private static final QPointTransactionSummaryEntity summary = QPointTransactionSummaryEntity.pointTransactionSummaryEntity;  // ✅ 별칭
    private final JPAQueryFactory queryFactory;

    public List<PointTransactionSummaryEntity> findByReference(Long userId, PointReference reference, String referenceKey) {
        return queryFactory
                .selectFrom(summary)
                .where(
                        summary.userId.eq(userId),
                        summary.reference.eq(reference),
                        summary.referenceKey.eq(referenceKey)
                )
                .fetch();
    }
}
