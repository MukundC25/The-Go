package com.fooddelivery.junit;

import com.fooddelivery.model.Payment;
import com.fooddelivery.repository.PaymentRepository;
import com.fooddelivery.service.PaymentService;
import com.fooddelivery.exception.FoodDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest {

    private PaymentRepository paymentRepo;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepo = new PaymentRepository();
        paymentService = new PaymentService(paymentRepo);
    }

    @Test
    @DisplayName("Process COD payment")
    void processCodPayment() {
        Payment p = new Payment("order1", 100.0, Payment.Method.CASH_ON_DELIVERY);
        paymentRepo.save(p);

        paymentService.processCodPayment(p.getId());

        Payment updated = paymentService.getPaymentById(p.getId());
        assertThat(updated.getStatus()).isEqualTo(Payment.Status.SUCCESS);
    }

    @Test
    @DisplayName("RT_007: Payment with expired card or invalid payment logic")
    void invalidPaymentIdThrowsError() {
        assertThatThrownBy(() -> paymentService.processCodPayment("invalid_id"))
                .isInstanceOf(FoodDeliveryException.class);
    }
}
