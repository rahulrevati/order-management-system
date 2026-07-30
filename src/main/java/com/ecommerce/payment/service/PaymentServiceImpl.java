//package com.ecommerce.payment.service;
//
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import com.ecommerce.order.entity.Order;
//import com.ecommerce.order.repository.OrderRepository;
//import com.ecommerce.payment.dto.PaymentRequest;
//import com.ecommerce.payment.dto.PaymentResponse;
//import com.ecommerce.payment.entity.Payment;
//import com.ecommerce.payment.mapper.PaymentMapper;
//import com.ecommerce.payment.repository.PaymentRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentServiceImpl implements PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    private final OrderRepository orderRepository;
//
//    @Override
//    @Transactional
//    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
//        Order order = orderRepository.findById(paymentRequest.getOrderId())
//                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", paymentRequest.getOrderId()));
//
//        Payment payment = Payment.builder()
//                .order(order)
//                .amount(order.getTotalAmount())
//                .paymentMethod(order.getPaymentMethod())
//                .status(PaymentStatus.PENDING)
//                .transactionId(UUID.randomUUID().toString())
//                .paymentDate(LocalDateTime.now())
//                .build();
//
//        Payment savedPayment = paymentRepository.save(payment);
//
//        return PaymentMapper.toResponse(savedPayment);
//    }
//
//    @Override
//    public PaymentResponse getPaymentById(Long id) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
//        return PaymentMapper.toResponse(payment);
//    }
//
//    @Override
//    public PaymentResponse getPaymentByOrderId(Long orderId) {
//        Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
//        return PaymentMapper.toResponse(payment);
//    }
//
//    @Override
//    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
//        List<Payment> payments = paymentRepository.findByStatus(status);
//        return payments.stream()
//                .map(PaymentMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public List<PaymentResponse> getPaymentsByMethod(PaymentMethod method) {
//        List<Payment> payments = paymentRepository.findByPaymentMethod(method);
//        return payments.stream()
//                .map(PaymentMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public PaymentResponse updatePaymentStatus(Long id, PaymentStatus status) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
//        payment.setStatus(status);
//        Payment updatedPayment = paymentRepository.save(payment);
//        return PaymentMapper.toResponse(updatedPayment);
//    }
//
//    @Override
//    @Transactional
//    public PaymentResponse refundPayment(Long id) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
//        payment.setStatus(PaymentStatus.REFUNDED);
//        payment.setRefundDate(LocalDateTime.now());
//        Payment updatedPayment = paymentRepository.save(payment);
//        return PaymentMapper.toResponse(updatedPayment);
//    }
//}
