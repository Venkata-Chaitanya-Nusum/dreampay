package com.dreampay.dreampay_backend.service;

import com.dreampay.dreampay_backend.entity.Wallet;
import com.dreampay.dreampay_backend.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // ✅ CREDIT
    public void credit(String userEmail, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = walletRepository
                .findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUserEmail(userEmail);
                    newWallet.setBalance(BigDecimal.ZERO);
                    return newWallet;
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    // ✅ DEBIT
    public void debit(String userEmail, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = walletRepository
                .findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }
}
