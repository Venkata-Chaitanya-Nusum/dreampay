package com.dreampay.dreampay_backend.service;

import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.dreampay.dreampay_backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> findByStripeId(String stripeId) {
        return transactionRepository.findByProviderPaymentId(stripeId);
    }

    // 🔐 IDPOTENCY SAFE SUCCESS
    public boolean markSuccessIfNotProcessed(String stripeId) {

        Optional<Transaction> optional = findByStripeId(stripeId);

        if (optional.isEmpty()) {
            System.out.println("❌ Transaction not found for: " + stripeId);
            return false;
        }

        Transaction tx = optional.get();

        // 🛑 Already processed
        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            System.out.println("⚠️ Already marked SUCCESS");
            return false;
        }

        tx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(tx);

        return true;
    }

    // ❌ Mark Failed
    public void markFailed(String stripeId) {

        Optional<Transaction> optional = findByStripeId(stripeId);

        optional.ifPresent(tx -> {
            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);
        });
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
}
