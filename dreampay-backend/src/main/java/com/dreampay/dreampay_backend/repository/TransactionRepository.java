package com.dreampay.dreampay_backend.repository;



import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    Optional<Transaction> findByProviderPaymentId(String providerPaymentId);
    @Modifying
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.providerPaymentId = :stripeId")
    int updateStatusByStripeId(@Param("stripeId") String stripeId,
                               @Param("status") TransactionStatus status);



}

