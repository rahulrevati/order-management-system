package com.ecommerce.order.service;

import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.exception.*;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    private OrderItem orderItem;
    private CartItem cartItem;
    private Product product;
    private UpdateOrderStatusRequest updateStatusRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setActive(true);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("999.99"));
        cartItem.setTotalPrice(new BigDecimal("1999.98"));

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("999.99"));
        orderItem.setTotalPrice(new BigDecimal("1999.98"));

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-123456789");
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("1999.98"));
        order.setOrderItems(Arrays.asList(orderItem));

        updateStatusRequest = new UpdateOrderStatusRequest();
        updateStatusRequest.setStatus(OrderStatus.CONFIRMED);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void placeOrder_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse response = orderService.placeOrder();

        assertNotNull(response);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).deleteByUser(any(User.class));
    }

    @Test
    void placeOrder_EmptyCart() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(new ArrayList<>());

        assertThrows(IllegalStateException.class, () -> orderService.placeOrder());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_ProductInactive() {
        product.setActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));

        assertThrows(ProductInactiveException.class, () -> orderService.placeOrder());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_InsufficientStock() {
        product.setStockQuantity(1);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse response = orderService.updateOrderStatus(1L, updateStatusRequest);

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(1L, updateStatusRequest));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_InvalidTransition() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrderStatus(1L, updateStatusRequest));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getMyOrders_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(order));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        List<OrderResponse> responses = orderService.getMyOrders();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(orderRepository).findByUser(any(User.class));
    }

    @Test
    void getOrderById_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_OrderNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrderById_UnauthorizedAccess() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setUser(otherUser);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void cancelOrder_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse response = orderService.cancelOrder(1L);

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelOrder_OrderNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void cancelOrder_CannotCancelShippedOrder() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_CannotCancelDeliveredOrder() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_AlreadyCancelled() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any(Order.class));
    }
}
