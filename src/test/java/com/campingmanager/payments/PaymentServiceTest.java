package com.campingmanager.payments;

import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeRental;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.email.EmailService;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.payments.dto.PaymentDTO;
import com.campingmanager.payments.entity.Payment;
import com.campingmanager.payments.entity.PaymentStatus;
import com.campingmanager.payments.repository.PaymentRepository;
import com.campingmanager.payments.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BikeRentalRepository rentalRepository;
    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentService service;

    private Payment pendingPayment() {
        Bike bike = new Bike();
        bike.setId(1L);
        bike.setStatus(BikeStatus.DISPONIBILE);

        BikeRental rental = new BikeRental();
        rental.setId(2L);
        rental.setBike(bike);
        rental.setStatus(BikeRentalStatus.PENDING_PAYMENT);

        Payment payment = new Payment();
        payment.setId(3L);
        payment.setBikeRental(rental);
        payment.setStripeSessionId("sess_123");
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    @Test
    void shouldConfirmPaymentAndActivateRental() {
        Payment payment = pendingPayment();
        when(paymentRepository.findByStripeSessionId("sess_123")).thenReturn(Optional.of(payment));
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bikeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentDTO dto = service.handlePaymentSuccess("sess_123");

        assertThat(dto.getStatus()).isEqualTo("PAID");
        assertThat(payment.getBikeRental().getStatus()).isEqualTo(BikeRentalStatus.ATTIVO);
        assertThat(payment.getBikeRental().getBike().getStatus()).isEqualTo(BikeStatus.NOLEGGIATA);
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    void shouldBeIdempotentWhenAlreadyPaid() {
        Payment payment = pendingPayment();
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findByStripeSessionId("sess_123")).thenReturn(Optional.of(payment));

        PaymentDTO dto = service.handlePaymentSuccess("sess_123");

        assertThat(dto.getStatus()).isEqualTo("PAID");
    }

    @Test
    void shouldThrowWhenSessionUnknown() {
        when(paymentRepository.findByStripeSessionId("nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.handlePaymentSuccess("nope"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
