package org.app.credit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.credit.entities.enums.RequestStateEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Column(name = "period_days")
    private Integer periodDays;

    private LocalDateTime date;

    private String reason;

    @Enumerated(EnumType.STRING)
    private RequestStateEnum state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
