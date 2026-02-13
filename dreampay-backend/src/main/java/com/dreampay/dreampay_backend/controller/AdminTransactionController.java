package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.dreampay.dreampay_backend.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @PatchMapping("/{id}/status")
    public Transaction updateStatus(@PathVariable Long id,
                                    @RequestParam TransactionStatus status) {

        Optional<Transaction> optionalTx = transactionRepository.findById(id);

        if (optionalTx.isEmpty()) {
            throw new RuntimeException("Transaction not found");
        }

        Transaction tx = optionalTx.get();

        // Allow only valid transitions
        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Only PENDING transactions can be updated");
        }

        tx.setStatus(status);

        return transactionRepository.save(tx);
    }
}
