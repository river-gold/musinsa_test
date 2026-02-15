package musinsa.test.entity.point;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.Auditable;
import musinsa.test.entity.AuditableEntityListener;
import musinsa.test.entity.point.converter.PointEarnTypeConverter;
import musinsa.test.entity.point.converter.PointStatusConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "point",
        indexes = {@Index(name = "idx_user_id", columnList = "user_id, status, expire_date")}
)
@EntityListeners(AuditableEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PointEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @With
    private Long id;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String pointKey;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal earnedAmount;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private LocalDate expireDate;

    @Convert(converter = PointStatusConverter.class)
    @Column(nullable = false)
    private PointStatus status;

    @Convert(converter = PointEarnTypeConverter.class)
    @Column(nullable = false)
    private PointEarnType earnType;

    private String issuerId;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
