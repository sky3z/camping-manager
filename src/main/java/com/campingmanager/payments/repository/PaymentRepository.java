package com.campingmanager.payments.repository;

import com.campingmanager.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    Optional<Payment> findByBikeRentalId(Long bikeRentalId);

    /**
     * Fatturato dei noleggi pagati, aggregato per anno/mese.
     * Ritorna righe [anno (Integer), mese (Integer), totale (BigDecimal)].
     */
    @Query("""
            SELECT YEAR(p.paidAt), MONTH(p.paidAt), SUM(p.amount)
            FROM Payment p
            WHERE p.status = com.campingmanager.payments.entity.PaymentStatus.PAID
            GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
            ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)
            """)
    List<Object[]> revenueByMonth();
}
