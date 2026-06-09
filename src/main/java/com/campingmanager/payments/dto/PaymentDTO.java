package com.campingmanager.payments.dto;

import com.campingmanager.payments.entity.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {

    private Long id;
    private Long bikeRentalId;
    private String stripeSessionId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime paidAt;

    public static PaymentDTO from(Payment p) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(p.getId());
        if (p.getBikeRental() != null) {
            dto.setBikeRentalId(p.getBikeRental().getId());
        }
        dto.setStripeSessionId(p.getStripeSessionId());
        dto.setAmount(p.getAmount());
        dto.setCurrency(p.getCurrency());
        dto.setStatus(p.getStatus().name());
        dto.setPaidAt(p.getPaidAt());
        return dto;
    }
}
