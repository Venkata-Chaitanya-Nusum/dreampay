package com.dreampay.dreampay_backend.service;

import com.dreampay.dreampay_backend.entity.Transaction;
import com.dreampay.dreampay_backend.entity.TransactionStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookService {

    private final String endpointSecret;
    private final TransactionService transactionService;
    private final WalletService walletService;

    public WebhookService(
            @Value("${stripe.webhook.secret}") String endpointSecret,
            TransactionService transactionService,
            WalletService walletService) {

        this.endpointSecret = endpointSecret;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    public void processStripeWebhook(String payload, String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Stripe signature");
        }

        System.out.println("🔥 Event type: " + event.getType());

        switch (event.getType()) {

            case "payment_intent.succeeded":
                handleSuccess(event);
                break;

            case "payment_intent.payment_failed":
                handleFailure(event);
                break;

            default:
                System.out.println("ℹ️ Unhandled event type");
        }
    }

    private void handleSuccess(Event event) {

        PaymentIntent paymentIntent =
                (PaymentIntent) event.getData().getObject();

        String stripeId = paymentIntent.getId();

        Optional<Transaction> optionalTx =
                transactionService.findByStripeId(stripeId);

        if (optionalTx.isPresent()) {

            Transaction tx = optionalTx.get();

            boolean updated =
                    transactionService.markSuccessIfNotProcessed(stripeId);

            if (updated) {
                walletService.credit(tx.getUserEmail(), tx.getAmount());
                System.out.println("✅ SUCCESS & Wallet credited");
            }
        }
    }

    private void handleFailure(Event event) {

        PaymentIntent paymentIntent =
                (PaymentIntent) event.getData().getObject();

        String stripeId = paymentIntent.getId();

        transactionService.markFailed(stripeId);

        System.out.println("❌ Marked FAILED");
    }
}
