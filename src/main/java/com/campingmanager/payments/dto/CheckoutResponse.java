package com.campingmanager.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Risposta alla creazione del checkout Stripe: l'ospite va su {@code checkoutUrl} per pagare.
 */
@Data
@AllArgsConstructor
public class CheckoutResponse {
    private String checkoutUrl;
    private String sessionId;
    private Long paymentId;
}
