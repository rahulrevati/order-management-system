//package com.ecommerce.payment.service;
//
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import com.ecommerce.payment.dto.PaymentRequest;
//import com.ecommerce.payment.dto.PaymentResponse;
//
//import java.util.List;
//
//public interface PaymentService {
//
//    PaymentResponse processPayment(PaymentRequest paymentRequest);
//
//    PaymentResponse getPaymentById(Long id);
//
//    PaymentResponse getPaymentByOrderId(Long orderId);
//
//    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
//
//    List<PaymentResponse> getPaymentsByMethod(PaymentMethod method);
//
//    PaymentResponse updatePaymentStatus(Long id, PaymentStatus status);
//
//    PaymentResponse refundPayment(Long id);
//}
