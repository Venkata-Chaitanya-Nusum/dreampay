package com.dreampay.dreampay_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stripeRefundId;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    @ManyToOne
    private Transaction transaction;
}

