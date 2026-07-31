package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder();

    List<OrderResponse> getMyOrders();

    OrderResponse getOrderById(Long orderId);

    OrderResponse cancelOrder(Long orderId);

    OrderResponse updateOrderStatus(Long orderId,
                                    UpdateOrderStatusRequest request);
}