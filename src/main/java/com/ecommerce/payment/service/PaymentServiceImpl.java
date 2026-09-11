package com.ecommerce.payment.service;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.enums.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final UserRepository userRepository;

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

        authorizeOrderAccess(payment.getOrder());

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

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Payment cannot be processed for a cancelled order");
        }

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException(
                    "Payment already exists for this order");
        }
    }

    private Order getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        authorizeOrderAccess(order);
        return order;
    }

    private void authorizeOrderAccess(Order order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() != null
                && user.getRole().getRoleName() == RoleName.ADMIN;

        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
    }
}
