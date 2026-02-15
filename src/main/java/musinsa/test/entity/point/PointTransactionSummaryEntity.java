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
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.entity.Auditable;
import musinsa.test.entity.AuditableEntityListener;
import musinsa.test.entity.point.converter.PointActionConverter;
import musinsa.test.entity.point.converter.PointReferenceConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "point_transaction_summary",
        indexes = {@Index(name = "idx_reference", columnList = "user_id, reference, reference_key")}
)
@EntityListeners(AuditableEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PointTransactionSummaryEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @Convert(converter = PointActionConverter.class)
    private PointAction action;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal beforeSumBalance;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal sumBalance;

    @Convert(converter = PointReferenceConverter.class)
    private PointReference reference;

    private String referenceKey;

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
