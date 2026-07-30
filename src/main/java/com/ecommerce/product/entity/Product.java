//package com.ecommerce.product.entity;
//
//import com.ecommerce.category.entity.Category;
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
//@Table(name = "products")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Product {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//
//    @Column(nullable = false)
//    private Integer stockQuantity;
//
//    @Column(name = "image_url")
//    private String imageUrl;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private List<com.ecommerce.cart.entity.CartItem> cartItems = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private List<com.ecommerce.order.entity.OrderItem> orderItems = new ArrayList<>();
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
