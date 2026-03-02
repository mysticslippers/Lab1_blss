package me.ifmo.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @Column(name = "provider_payment_id", nullable = false, unique = true)
    private String providerPaymentId;

    @Column(name = "amount_cents", nullable = false)
    private Integer amountCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus status;

    @Column(name = "retry_count", nullable = false)
    private Short retryCount;

    @Column(name = "max_retries", nullable = false)
    private Short maxRetries;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        Long enrollmentId = (enrollment != null ? enrollment.getId() : null);

        return "Payment{" +
                "id=" + id +
                ", enrollmentId=" + enrollmentId +
                ", providerPaymentId='" + providerPaymentId + '\'' +
                ", amountCents=" + amountCents +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", maxRetries=" + maxRetries +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Payment other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}