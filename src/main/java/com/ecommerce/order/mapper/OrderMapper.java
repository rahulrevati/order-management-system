//package com.ecommerce.order.mapper;
//
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.entity.Order;
//import com.ecommerce.order.entity.OrderItem;
//
//import java.util.List;
//
//public class OrderMapper {
//
//    public static OrderResponse toResponse(Order order) {
//        List<OrderResponse.OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
//                .map(OrderMapper::toOrderItemResponse)
//                .toList();
//
//        return OrderResponse.builder()
//                .id(order.getId())
//                .userId(order.getUser().getId())
//                .userEmail(order.getUser().getEmail())
//                .orderDate(order.getOrderDate())
//                .status(order.getStatus())
//                .paymentMethod(order.getPaymentMethod())
//                .paymentStatus(order.getPaymentStatus())
//                .totalAmount(order.getTotalAmount())
//                .shippingAddress(order.getShippingAddress())
//                .orderItems(orderItemResponses)
//                .createdAt(order.getCreatedAt())
//                .updatedAt(order.getUpdatedAt())
//                .build();
//    }
//
//    private static OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
//        return OrderResponse.OrderItemResponse.builder()
//                .id(orderItem.getId())
//                .productId(orderItem.getProduct().getId())
//                .productName(orderItem.getProduct().getName())
//                .quantity(orderItem.getQuantity())
//                .price(orderItem.getPrice())
//                .subtotal(orderItem.getSubtotal())
//                .build();
//    }
//}
