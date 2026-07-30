//package com.ecommerce.inventory.mapper;
//
//import com.ecommerce.inventory.dto.InventoryRequest;
//import com.ecommerce.inventory.dto.InventoryResponse;
//import com.ecommerce.inventory.entity.Inventory;
//
//public class InventoryMapper {
//
//    public static Inventory toEntity(InventoryRequest inventoryRequest) {
//        return Inventory.builder()
//                .quantity(inventoryRequest.getQuantity())
//                .reservedQuantity(inventoryRequest.getReservedQuantity())
//                .warehouseLocation(inventoryRequest.getWarehouseLocation())
//                .build();
//    }
//
//    public static InventoryResponse toResponse(Inventory inventory) {
//        int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
//
//        return InventoryResponse.builder()
//                .id(inventory.getId())
//                .productId(inventory.getProduct() != null ? inventory.getProduct().getId() : null)
//                .productName(inventory.getProduct() != null ? inventory.getProduct().getName() : null)
//                .quantity(inventory.getQuantity())
//                .reservedQuantity(inventory.getReservedQuantity())
//                .availableQuantity(availableQuantity)
//                .warehouseLocation(inventory.getWarehouseLocation())
//                .createdAt(inventory.getCreatedAt())
//                .updatedAt(inventory.getUpdatedAt())
//                .build();
//    }
//}
