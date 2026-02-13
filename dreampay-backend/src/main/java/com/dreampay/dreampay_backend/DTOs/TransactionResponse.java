package com.dreampay.dreampay_backend.DTOs;


import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String referenceId;
    private String paymentIntentId;   // 🔥 add this
    private String clientSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}

