package com.ecommerce.payment.service;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;

import com.ecommerce.payment.dto.CreatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.mapper.PaymentMapper;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse makePayment(CreatePaymentRequest request) {

        Order order = getOrder(request.getOrderId());

        validatePayment(order);

        Payment payment = new Payment();

        payment.setPaymentId(generatePaymentId());
        payment.setTransactionId(generateTransactionId());
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.CONFIRMED);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(Long orderId) {

        Order order = getOrder(orderId);

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    private String generatePaymentId() {

        return "PAY-" + System.currentTimeMillis();
    }

    private String generateTransactionId() {

        return UUID.randomUUID().toString();
    }
    private void validatePayment(Order order) {

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException(
                    "Payment already exists for this order");
        }
    }

    private Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));
    }
}
