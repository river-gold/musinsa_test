package musinsa.test.entity.point.repo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointEntity;
import musinsa.test.entity.point.QPointEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static musinsa.test.entity.point.QPointEntity.pointEntity;

@Repository
@RequiredArgsConstructor
public class PointQuerydslRepository {
    private static final QPointEntity point = pointEntity;  // ✅ 별칭
    private final JPAQueryFactory queryFactory;

    public PointEntity findByKeyWithLock(String pointKey) {
        return queryFactory
                .selectFrom(point)
                .where(
                        point.pointKey.eq(pointKey)
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
    }

    public List<PointEntity> findAllByUserWithLock(Long userId) {
        return queryFactory
                .selectFrom(point)
                .where(
                        point.userId.eq(userId),
                        point.status.eq(PointStatus.EARNED),
                        point.expireDate.goe(LocalDate.now())
                )
                .orderBy(point.id.asc())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    public List<PointEntity> findAll(List<Long> ids) {
        return queryFactory
                .selectFrom(point)
                .where(
                        point.id.in(ids)
                )
                .orderBy(point.id.asc())
                .fetch();
    }
}
