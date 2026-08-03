package com.ecommerce.payment.service;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.PaymentMethod;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.payment.dto.CreatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.mapper.PaymentMapper;
import com.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private Payment payment;
    private CreatePaymentRequest createPaymentRequest;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-123456789");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("1999.98"));

        payment = new Payment();
        payment.setId(1L);
        payment.setPaymentId("PAY-123456789");
        payment.setTransactionId("transaction-uuid");
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("1999.98"));
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setOrderId(1L);
        createPaymentRequest.setPaymentMethod(PaymentMethod.CARD);
    }

    @Test
    void makePayment_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(any(Order.class))).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.makePayment(createPaymentRequest);

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void makePayment_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.makePayment(createPaymentRequest));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void makePayment_PaymentAlreadyExists() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () -> paymentService.makePayment(createPaymentRequest));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getPayment_Success() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.getPayment(1L);

        assertNotNull(response);
        verify(paymentRepository).findById(1L);
    }

    @Test
    void getPayment_PaymentNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPayment(1L));
    }

    @Test
    void getPaymentByOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.getPaymentByOrder(1L);

        assertNotNull(response);
        verify(orderRepository).findById(1L);
        verify(paymentRepository).findByOrder(any(Order.class));
    }

    @Test
    void getPaymentByOrder_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByOrder(1L));

        verify(paymentRepository, never()).findByOrder(any(Order.class));
    }

    @Test
    void getPaymentByOrder_PaymentNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(any(Order.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByOrder(1L));
    }
}
