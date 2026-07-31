package com.ecommerce.order.dto;

import com.ecommerce.common.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime orderDate;

    private List<OrderItemResponse> items;
}