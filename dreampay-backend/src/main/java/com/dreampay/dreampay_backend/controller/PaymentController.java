package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.DTOs.PaymentRequest;
import com.dreampay.dreampay_backend.DTOs.TransactionResponse;
import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.dreampay.dreampay_backend.repository.TransactionRepository;
import com.dreampay.dreampay_backend.service.PaymentService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public TransactionResponse createPayment(
            @RequestBody PaymentRequest request,
            Authentication authentication) throws Exception {

        return paymentService.createPayment(
                request,
                authentication.getName()
        );
    }
}
