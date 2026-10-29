
package com.Projects.AirBnbApp.controller;

import com.Projects.AirBnbApp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payments")
    public ResponseEntity<Void> capturePayments(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        log.info("Stripe webhook endpoint reached");

        final Event event;

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed", e);
            return ResponseEntity.badRequest().build();
        }

        log.info("Stripe webhook received: type={}, id={}",
                event.getType(), event.getId());

        try {
            bookingService.capturePayment(event);
            log.info("Stripe webhook processed successfully: {}",
                    event.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to process Stripe webhook: type={}, id={}",
                    event.getType(), event.getId(), e);

            // Return a server error so Stripe CLI can report the failure
            // and Stripe can retry delivery where applicable.
            return ResponseEntity.internalServerError().build();
        }
    }
}
