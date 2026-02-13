package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.dreampay.dreampay_backend.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionRepository transactionRepository;

    public AdminController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {

        List<Transaction> all = transactionRepository.findAll();

        long totalTransactions = all.size();

        long successCount = all.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .count();

        long failedCount = all.stream()
                .filter(t -> t.getStatus() == TransactionStatus.FAILED)
                .count();

        long pendingCount = all.stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING)
                .count();

        BigDecimal totalRevenue = all.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("totalTransactions", totalTransactions);
        response.put("successCount", successCount);
        response.put("failedCount", failedCount);
        response.put("pendingCount", pendingCount);
        response.put("totalRevenue", totalRevenue);

        return response;
    }

    @PostMapping("/refund/{id}")
    public String refundTransaction(@PathVariable Long id) throws Exception {

        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.SUCCESS) {
            return "Only SUCCESS transactions can be refunded";
        }

        // Call Stripe Refund API
        Map<String, Object> params = new HashMap<>();
        params.put("payment_intent", tx.getProviderPaymentId());

        com.stripe.model.Refund refund =
                com.stripe.model.Refund.create(params);

        // Update DB
        tx.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(tx);

        return "Refund successful for transaction ID: " + id;
    }

}

