package musinsa.test.entity;

import java.time.LocalDateTime;

public interface Auditable {

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    String getUpdatedBy();

    void setUpdatedBy(String createdBy);

    LocalDateTime getUpdatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);
}
