//package com.ecommerce.order.entity;
//
//import com.ecommerce.auth.entity.User;
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "orders")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Column(name = "order_date", nullable = false)
//    private LocalDateTime orderDate;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private OrderStatus status;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentMethod paymentMethod;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentStatus paymentStatus;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal totalAmount;
//
//    @Column(nullable = false)
//    private String shippingAddress;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @Builder.Default
//    private List<OrderItem> orderItems = new ArrayList<>();
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}
