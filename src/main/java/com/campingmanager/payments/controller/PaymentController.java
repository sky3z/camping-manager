package com.campingmanager.payments.controller;

import com.campingmanager.payments.dto.CheckoutResponse;
import com.campingmanager.payments.dto.PaymentDTO;
import com.campingmanager.payments.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    /**
     * Crea la sessione di pagamento per un noleggio. L'ospite usa l'URL restituito per pagare.
     */
    @PostMapping("/rentals/{rentalId}/checkout")
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long rentalId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createCheckout(rentalId));
    }

    /**
     * Redirect di successo da Stripe: conferma il pagamento.
     */
    @GetMapping("/success")
    public ResponseEntity<PaymentDTO> success(@RequestParam("session_id") String sessionId) {
        return ResponseEntity.ok(paymentService.handlePaymentSuccess(sessionId));
    }

    /**
     * Webhook Stripe: alla conferma del pagamento porta il noleggio ad ATTIVO.
     * Processato solo se la firma e configurata e valida.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload,
                                          @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        if (webhookSecret == null || webhookSecret.isBlank() || signature == null) {
            return ResponseEntity.ok("Webhook ignorato (firma non configurata)");
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Firma webhook non valida");
        }
        if ("checkout.session.completed".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                if (obj instanceof Session session) {
                    paymentService.handlePaymentSuccess(session.getId());
                }
            });
        }
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }
}
