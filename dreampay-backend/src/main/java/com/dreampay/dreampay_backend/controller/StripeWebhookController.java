package com.dreampay.dreampay_backend.controller;

import com.dreampay.dreampay_backend.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class StripeWebhookController {

    private final WebhookService webhookService;

    public StripeWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {

            webhookService.processStripeWebhook(payload, sigHeader);

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (RuntimeException e) {

            System.out.println("❌ Webhook error: " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Webhook processing failed");
        }
    }
}
