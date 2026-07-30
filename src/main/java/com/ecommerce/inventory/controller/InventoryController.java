//package com.ecommerce.inventory.controller;
//
//import com.ecommerce.inventory.dto.InventoryRequest;
//import com.ecommerce.inventory.dto.InventoryResponse;
//import com.ecommerce.inventory.service.InventoryService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/inventory")
//@RequiredArgsConstructor
//public class InventoryController {
//
//    private final InventoryService inventoryService;
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {
//        InventoryResponse inventoryResponse = inventoryService.createInventory(inventoryRequest);
//        return new ResponseEntity<>(inventoryResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
//        InventoryResponse inventoryResponse = inventoryService.getInventoryById(id);
//        return ResponseEntity.ok(inventoryResponse);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
//        List<InventoryResponse> inventoryList = inventoryService.getAllInventory();
//        return ResponseEntity.ok(inventoryList);
//    }
//
//    @GetMapping("/product/{productId}")
//    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
//        InventoryResponse inventoryResponse = inventoryService.getInventoryByProductId(productId);
//        return ResponseEntity.ok(inventoryResponse);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<InventoryResponse> updateInventory(
//            @PathVariable Long id,
//            @Valid @RequestBody InventoryRequest inventoryRequest) {
//        InventoryResponse inventoryResponse = inventoryService.updateInventory(id, inventoryRequest);
//        return ResponseEntity.ok(inventoryResponse);
//    }
//
//    @PatchMapping("/{id}/stock")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<InventoryResponse> updateStock(
//            @PathVariable Long id,
//            @RequestParam Integer quantity) {
//        InventoryResponse inventoryResponse = inventoryService.updateStock(id, quantity);
//        return ResponseEntity.ok(inventoryResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
//        inventoryService.deleteInventory(id);
//        return ResponseEntity.noContent().build();
//    }
//}
