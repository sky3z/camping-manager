package com.campingmanager.payments.service;

import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeRental;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.payments.dto.CheckoutResponse;
import com.campingmanager.payments.dto.PaymentDTO;
import com.campingmanager.payments.entity.Payment;
import com.campingmanager.payments.entity.PaymentStatus;
import com.campingmanager.payments.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BikeRentalRepository rentalRepository;
    private final BikeRepository bikeRepository;

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Crea una sessione di pagamento Stripe per un noleggio in attesa di pagamento.
     */
    public CheckoutResponse createCheckout(Long rentalId) {
        BikeRental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato con id: " + rentalId));

        if (rental.getStatus() != BikeRentalStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Il noleggio non e in attesa di pagamento");
        }
        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            throw new BadRequestException("Stripe non e configurato (manca STRIPE_SECRET_KEY)");
        }

        Stripe.apiKey = stripeApiKey;
        long amountCents = rental.getTotalPrice().movePointRight(2).longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/api/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/api/payments/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(amountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Noleggio bici " + rental.getBike().getCode())
                                        .build())
                                .build())
                        .build())
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new IllegalStateException("Errore nella creazione della sessione di pagamento Stripe", e);
        }

        Payment payment = new Payment();
        payment.setBikeRental(rental);
        payment.setStripeSessionId(session.getId());
        payment.setAmount(rental.getTotalPrice());
        payment.setCurrency("eur");
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return new CheckoutResponse(session.getUrl(), session.getId(), payment.getId());
    }

    /**
     * Conferma il pagamento (chiamata dal webhook o dalla redirect di successo):
     * porta il Payment a PAID, il noleggio ad ATTIVO e la bici a NOLEGGIATA. Idempotente.
     */
    public PaymentDTO handlePaymentSuccess(String sessionId) {
        Payment payment = paymentRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pagamento non trovato per la sessione: " + sessionId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return PaymentDTO.from(payment);
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        BikeRental rental = payment.getBikeRental();
        rental.setStatus(BikeRentalStatus.ATTIVO);
        rentalRepository.save(rental);

        Bike bike = rental.getBike();
        bike.setStatus(BikeStatus.NOLEGGIATA);
        bikeRepository.save(bike);

        return PaymentDTO.from(paymentRepository.save(payment));
    }

    public PaymentDTO getById(Long id) {
        return paymentRepository.findById(id)
                .map(PaymentDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento non trovato con id: " + id));
    }
}
