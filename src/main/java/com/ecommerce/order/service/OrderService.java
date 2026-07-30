//package com.ecommerce.order.service;
//
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.order.dto.OrderRequest;
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.dto.OrderStatusResponse;
//
//import java.util.List;
//
//public interface OrderService {
//
//    OrderResponse createOrder(OrderRequest orderRequest, String userEmail);
//
//    OrderResponse getOrderById(Long id);
//
//    List<OrderResponse> getUserOrders(String userEmail);
//
//    List<OrderResponse> getOrdersByStatus(OrderStatus status);
//
//    OrderStatusResponse updateOrderStatus(Long id, OrderStatus status);
//
//    void deleteOrder(Long id);
//}
