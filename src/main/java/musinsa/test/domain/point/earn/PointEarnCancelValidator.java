package musinsa.test.domain.point.earn;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointStatus;

import java.util.Optional;

@RequiredArgsConstructor
@Builder
public class PointEarnCancelValidator {
    private final Optional<Point> point;

    public void validate(PointEarnCancelCommand command) {
        if (point.isEmpty()) {
            String message = "존재하지 않는 포인트 입니다. pointKey=%s".formatted(command.pointKey());
            throw new IllegalArgumentException(message);
        }

        Point point = this.point.get();
        if (point.status() != PointStatus.EARNED) {
            throw new IllegalArgumentException("현재 %s 상태입니다.".formatted(point.status().getDescription()));
        }

        if (!point.balance().equals(point.earnedAmount())) {
            throw new IllegalArgumentException("이미 사용된 포인트 입니다.");
        }
    }
}
