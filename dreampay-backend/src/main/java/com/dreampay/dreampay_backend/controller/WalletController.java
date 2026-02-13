package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.entity.Wallet;
import com.dreampay.dreampay_backend.repository.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/wallet")
public class WalletController {

    private final WalletRepository walletRepository;

    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @GetMapping
    public Wallet getWallet(Authentication authentication) {

        String email = authentication.getName();

        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}

