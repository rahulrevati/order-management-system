//package com.ecommerce.order.dto;
//
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class OrderResponse {
//
//    private Long id;
//    private Long userId;
//    private String userEmail;
//    private LocalDateTime orderDate;
//    private OrderStatus status;
//    private PaymentMethod paymentMethod;
//    private PaymentStatus paymentStatus;
//    private BigDecimal totalAmount;
//    private String shippingAddress;
//    private List<OrderItemResponse> orderItems;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class OrderItemResponse {
//        private Long id;
//        private Long productId;
//        private String productName;
//        private Integer quantity;
//        private BigDecimal price;
//        private BigDecimal subtotal;
//    }
//}
