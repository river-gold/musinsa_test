package musinsa.test.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.LocalDateTime;

@Configurable
public class AuditableEntityListener {
    @PrePersist
    public void touchForCreate(Object target) {
        if (target instanceof Auditable auditable) {
            auditable.setCreatedBy("SYSTEM");
            auditable.setCreatedAt(LocalDateTime.now());
        }

        touchForUpdate(target);
    }

    @PreUpdate
    public void touchForUpdate(Object target) {
        if (target instanceof Auditable auditable) {
            auditable.setUpdatedBy("SYSTEM");
            auditable.setUpdatedAt(LocalDateTime.now());
        }
    }
}
