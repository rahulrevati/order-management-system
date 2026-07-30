//package com.ecommerce.payment.mapper;
//
//import com.ecommerce.payment.dto.PaymentResponse;
//import com.ecommerce.payment.entity.Payment;
//
//public class PaymentMapper {
//
//    public static PaymentResponse toResponse(Payment payment) {
//        return PaymentResponse.builder()
//                .id(payment.getId())
//                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
//                .amount(payment.getAmount())
//                .paymentMethod(payment.getPaymentMethod())
//                .status(payment.getStatus())
//                .transactionId(payment.getTransactionId())
//                .paymentDate(payment.getPaymentDate())
//                .refundDate(payment.getRefundDate())
//                .createdAt(payment.getCreatedAt())
//                .updatedAt(payment.getUpdatedAt())
//                .build();
//    }
//}
