package org.app.credit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Min(1000)
    private BigDecimal amount;

    @NotNull
    @Min(1)
    @Column(name = "period_days")
    private Integer periodDays;

    @NotNull
    private LocalDateTime date;

    @NotBlank
    @Size(min = 20)
    private String reason;

    @Enumerated(EnumType.STRING)
    private RequestStateEnum state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}
