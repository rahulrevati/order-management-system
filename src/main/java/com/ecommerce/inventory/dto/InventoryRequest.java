//package com.ecommerce.inventory.dto;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class InventoryRequest {
//
//    @NotNull(message = "Product ID is required")
//    private Long productId;
//
//    @NotNull(message = "Quantity is required")
//    @Min(value = 0, message = "Quantity cannot be negative")
//    private Integer quantity;
//
//    @Min(value = 0, message = "Reserved quantity cannot be negative")
//    private Integer reservedQuantity = 0;
//
//    private String warehouseLocation;
//}
