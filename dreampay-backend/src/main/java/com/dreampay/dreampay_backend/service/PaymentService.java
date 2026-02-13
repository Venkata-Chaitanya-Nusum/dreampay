package com.dreampay.dreampay_backend.service;

import com.dreampay.dreampay_backend.DTOs.PaymentRequest;
import com.dreampay.dreampay_backend.DTOs.TransactionResponse;
import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.dreampay.dreampay_backend.repository.TransactionRepository;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse createPayment(
            PaymentRequest request,
            String userEmail) throws Exception {

        // 1️⃣ Create Stripe PaymentIntent
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(request.getAmount()
                                .multiply(new BigDecimal(100))
                                .longValue())
                        .setCurrency(request.getCurrency().toLowerCase())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .setAllowRedirects(
                                                PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                        )
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 2️⃣ Save Transaction
        Transaction tx = new Transaction();
        tx.setReferenceId(UUID.randomUUID().toString());
        tx.setAmount(request.getAmount());
        tx.setCurrency(request.getCurrency());
        tx.setStatus(TransactionStatus.PENDING);
        tx.setPaymentProvider("STRIPE");
        tx.setProviderPaymentId(paymentIntent.getId());
        tx.setCreatedAt(LocalDateTime.now());
        tx.setUserEmail(userEmail);

        Transaction saved = transactionRepository.save(tx);

        // 3️⃣ Convert to DTO
        TransactionResponse response = new TransactionResponse();
        response.setId(saved.getId());
        response.setAmount(saved.getAmount());
        response.setCurrency(saved.getCurrency());
        response.setStatus(saved.getStatus().name());
        response.setReferenceId(saved.getReferenceId());
        response.setPaymentIntentId(paymentIntent.getId());
        response.setClientSecret(paymentIntent.getClientSecret());

        return response;
    }
}
