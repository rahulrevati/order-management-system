//package com.ecommerce.inventory.entity;
//
//import com.ecommerce.product.entity.Product;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "inventory")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Inventory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false, unique = true)
//    private Product product;
//
//    @Column(nullable = false)
//    private Integer quantity;
//
//    @Column(name = "reserved_quantity")
//    private Integer reservedQuantity = 0;
//
//    @Column(name = "warehouse_location")
//    private String warehouseLocation;
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
