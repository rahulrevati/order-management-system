package com.ecommerce.order.service;

import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.RoleName;
import com.ecommerce.common.exception.*;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;

import com.ecommerce.email.EmailService;
import com.ecommerce.monitoring.BusinessMetrics;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final BusinessMetrics businessMetrics;

    @Override
    public OrderResponse placeOrder() {

        User user = getCurrentUser();

        List<CartItem> cartItems = getCartItems(user);

        validateCart(cartItems);

        Order order = createOrder(user, cartItems);

        cartRepository.deleteByUser(user);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = getOrderByIds(orderId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException(
                    "Only administrators can update order status");
        }

        validateStatusTransition(
                order.getStatus(),
                request.getStatus());

        order.setStatus(request.getStatus());

        return orderMapper.toResponse(order);
    }
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        return orderRepository.findByUser(user)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = getOrderEntity(orderId);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = getOrderEntity(orderId);

        validateOrderCancellation(order);

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(order);
    }

    private Order getOrderEntity(Long orderId) {

        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        boolean isAdmin = user.getRole() != null
                && user.getRole().getRoleName() == RoleName.ADMIN;

        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to access this order");
        }

        return order;
    }


    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private List<CartItem> getCartItems(User user) {

        List<CartItem> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        return cartItems;
    }
    private void validateCart(List<CartItem> cartItems) {

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (!product.getActive()) {
                throw new ProductInactiveException(
                        "Product is inactive : " + product.getName());
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product : "
                                + product.getName());
            }
        }
    }

    private Order getOrderByIds(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }
    private Order createOrder(User user,
                              List<CartItem> cartItems) {

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(cartItem.getTotalPrice());

            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        businessMetrics.incrementOrdersCreated();

        // Build order event for notifications
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .userId(user.getId())
                .email(user.getEmail())
                .totalAmount(savedOrder.getTotalAmount())
                .build();

        // Send order confirmation asynchronously
        emailService.sendOrderConfirmation(event);

        return savedOrder;

    }

    private void validateStatusTransition(OrderStatus currentStatus,
                                          OrderStatus newStatus) {

        switch (currentStatus) {

            case PENDING -> {
                if (newStatus != OrderStatus.CONFIRMED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidOrderStatusException(
                            "Invalid status transition");
                }
            }

            case CONFIRMED -> {
                if (newStatus != OrderStatus.SHIPPED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidOrderStatusException (
                            "Invalid status transition");
                }
            }

            case SHIPPED -> {
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new InvalidOrderStatusException (
                            "Invalid status transition");
                }
            }

            case DELIVERED, CANCELLED ->
                    throw new InvalidOrderStatusException(
                            "Order status cannot be changed");
        }
    }
    private void validateOrderCancellation(Order order) {

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {

            throw new OrderCancellationException(
                    "Order cannot be cancelled because it is " + order.getStatus());
        }
    }

    private void restoreStock(Order order) {

        for (OrderItem orderItem : order.getOrderItems()) {

            Product product = orderItem.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + orderItem.getQuantity()
            );
        }
    }
}