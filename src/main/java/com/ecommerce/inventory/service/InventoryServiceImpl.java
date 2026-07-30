//package com.ecommerce.inventory.service;
//
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import com.ecommerce.inventory.dto.InventoryRequest;
//import com.ecommerce.inventory.dto.InventoryResponse;
//import com.ecommerce.inventory.entity.Inventory;
//import com.ecommerce.inventory.mapper.InventoryMapper;
//import com.ecommerce.inventory.repository.InventoryRepository;
//import com.ecommerce.product.entity.Product;
//import com.ecommerce.product.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class InventoryServiceImpl implements InventoryService {
//
//    private final InventoryRepository inventoryRepository;
//    private final ProductRepository productRepository;
//
//    @Override
//    public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
//        Product product = productRepository.findById(inventoryRequest.getProductId())
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", inventoryRequest.getProductId()));
//
//        Inventory inventory = InventoryMapper.toEntity(inventoryRequest);
//        inventory.setProduct(product);
//        Inventory savedInventory = inventoryRepository.save(inventory);
//        return InventoryMapper.toResponse(savedInventory);
//    }
//
//    @Override
//    public InventoryResponse getInventoryById(Long id) {
//        Inventory inventory = inventoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
//        return InventoryMapper.toResponse(inventory);
//    }
//
//    @Override
//    public List<InventoryResponse> getAllInventory() {
//        List<Inventory> inventoryList = inventoryRepository.findAll();
//        return inventoryList.stream()
//                .map(InventoryMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public InventoryResponse getInventoryByProductId(Long productId) {
//        Inventory inventory = inventoryRepository.findByProductId(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
//        return InventoryMapper.toResponse(inventory);
//    }
//
//    @Override
//    public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
//        Inventory inventory = inventoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
//
//        Product product = productRepository.findById(inventoryRequest.getProductId())
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", inventoryRequest.getProductId()));
//
//        inventory.setProduct(product);
//        inventory.setQuantity(inventoryRequest.getQuantity());
//        inventory.setReservedQuantity(inventoryRequest.getReservedQuantity());
//        inventory.setWarehouseLocation(inventoryRequest.getWarehouseLocation());
//
//        Inventory updatedInventory = inventoryRepository.save(inventory);
//        return InventoryMapper.toResponse(updatedInventory);
//    }
//
//    @Override
//    public InventoryResponse updateStock(Long id, Integer quantity) {
//        Inventory inventory = inventoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
//        inventory.setQuantity(quantity);
//        Inventory updatedInventory = inventoryRepository.save(inventory);
//        return InventoryMapper.toResponse(updatedInventory);
//    }
//
//    @Override
//    public void deleteInventory(Long id) {
//        Inventory inventory = inventoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
//        inventoryRepository.delete(inventory);
//    }
//}
