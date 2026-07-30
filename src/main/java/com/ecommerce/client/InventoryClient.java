//package com.ecommerce.client;
//
//import com.ecommerce.inventory.dto.InventoryRequest;
//import com.ecommerce.inventory.dto.InventoryResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Component
//public class InventoryClient {
//
//    private final WebClient webClient;
//
//    public InventoryClient(WebClient.Builder webClientBuilder,
//                           @Value("${inventory.service.url:http://localhost:8083}") String inventoryServiceUrl) {
//        this.webClient = webClientBuilder
//                .baseUrl(inventoryServiceUrl)
//                .build();
//    }
//
//    public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
//        return webClient.post()
//                .uri("/api/inventory")
//                .bodyValue(inventoryRequest)
//                .retrieve()
//                .bodyToMono(InventoryResponse.class)
//                .block();
//    }
//
//    public InventoryResponse getInventoryById(Long inventoryId) {
//        return webClient.get()
//                .uri("/api/inventory/{id}", inventoryId)
//                .retrieve()
//                .bodyToMono(InventoryResponse.class)
//                .block();
//    }
//
//    public InventoryResponse getInventoryByProductId(Long productId) {
//        return webClient.get()
//                .uri("/api/inventory/product/{productId}", productId)
//                .retrieve()
//                .bodyToMono(InventoryResponse.class)
//                .block();
//    }
//
//    public InventoryResponse updateStock(Long inventoryId, Integer quantity) {
//        return webClient.patch()
//                .uri("/api/inventory/{id}/stock?quantity={quantity}", inventoryId, quantity)
//                .retrieve()
//                .bodyToMono(InventoryResponse.class)
//                .block();
//    }
//}
