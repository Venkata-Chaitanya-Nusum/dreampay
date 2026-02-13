package com.dreampay.dreampay_backend.repository;

import com.dreampay.dreampay_backend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserEmail(String userEmail);
}
