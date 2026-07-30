//package com.ecommerce.order.service;
//
//import com.ecommerce.auth.entity.User;
//import com.ecommerce.auth.repository.UserRepository;
//import com.ecommerce.cart.entity.Cart;
//import com.ecommerce.cart.repository.CartRepository;
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import com.ecommerce.order.dto.OrderRequest;
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.dto.OrderStatusResponse;
//import com.ecommerce.order.entity.Order;
//import com.ecommerce.order.entity.OrderItem;
//import com.ecommerce.order.event.OrderCreatedEvent;
//import com.ecommerce.order.mapper.OrderMapper;
//import com.ecommerce.order.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;
//    private final CartRepository cartRepository;
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    @Override
//    @Transactional
//    public OrderResponse createOrder(OrderRequest orderRequest, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", userEmail));
//
//        if (cart.getCartItems().isEmpty()) {
//            throw new ResourceNotFoundException("Cart items", "cart", cart.getId());
//        }
//
//        Order order = Order.builder()
//                .user(user)
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .paymentMethod(orderRequest.getPaymentMethod())
//                .paymentStatus(PaymentStatus.PENDING)
//                .shippingAddress(orderRequest.getShippingAddress())
//                .totalAmount(calculateTotalAmount(cart))
//                .build();
//
//        List<OrderItem> orderItems = cart.getCartItems().stream()
//                .map(cartItem -> OrderItem.builder()
//                        .order(order)
//                        .product(cartItem.getProduct())
//                        .quantity(cartItem.getQuantity())
//                        .price(cartItem.getPrice())
//                        .subtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
//                        .build())
//                .collect(Collectors.toList());
//
//        order.setOrderItems(orderItems);
//        Order savedOrder = orderRepository.save(order);
//
//        OrderCreatedEvent event = OrderCreatedEvent.builder()
//                .orderId(savedOrder.getId())
//                .userId(user.getId())
//                .totalAmount(savedOrder.getTotalAmount())
//                .orderDate(savedOrder.getOrderDate())
//                .build();
//
//        kafkaTemplate.send("order-created", event.toString());
//
//        return OrderMapper.toResponse(savedOrder);
//    }
//
//    @Override
//    public OrderResponse getOrderById(Long id) {
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
//        return OrderMapper.toResponse(order);
//    }
//
//    @Override
//    public List<OrderResponse> getUserOrders(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//        List<Order> orders = orderRepository.findByUser(user);
//        return orders.stream()
//                .map(OrderMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
//        List<Order> orders = orderRepository.findByStatus(status);
//        return orders.stream()
//                .map(OrderMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public OrderStatusResponse updateOrderStatus(Long id, OrderStatus status) {
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
//        order.setStatus(status);
//        orderRepository.save(order);
//
//        return OrderStatusResponse.builder()
//                .orderId(order.getId())
//                .status(order.getStatus())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Override
//    @Transactional
//    public void deleteOrder(Long id) {
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
//        orderRepository.delete(order);
//    }
//
//    private BigDecimal calculateTotalAmount(Cart cart) {
//        return cart.getCartItems().stream()
//                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//}
