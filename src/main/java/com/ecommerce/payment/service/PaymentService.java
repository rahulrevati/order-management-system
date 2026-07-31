package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.CreatePaymentRequest;

import com.ecommerce.payment.dto.PaymentResponse;



public interface PaymentService {

    PaymentResponse makePayment(CreatePaymentRequest request);

    PaymentResponse getPayment(Long paymentId);

    PaymentResponse getPaymentByOrder(Long orderId);
}
