package musinsa.test.domain.point;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PointKeyGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
