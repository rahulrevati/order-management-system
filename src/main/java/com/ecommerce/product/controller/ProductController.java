//package com.ecommerce.product.controller;
//
//import com.ecommerce.product.dto.ProductRequest;
//import com.ecommerce.product.dto.ProductResponse;
//import com.ecommerce.product.service.ProductService;
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
//@RequestMapping("/api/products")
//@RequiredArgsConstructor
//public class ProductController {
//
//    private final ProductService productService;
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
//        ProductResponse productResponse = productService.createProduct(productRequest);
//        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
//        ProductResponse productResponse = productService.getProductById(id);
//        return ResponseEntity.ok(productResponse);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ProductResponse>> getAllProducts() {
//        List<ProductResponse> products = productService.getAllProducts();
//        return ResponseEntity.ok(products);
//    }
//
//    @GetMapping("/category/{categoryId}")
//    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
//        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
//        return ResponseEntity.ok(products);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<ProductResponse> updateProduct(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductRequest productRequest) {
//        ProductResponse productResponse = productService.updateProduct(id, productRequest);
//        return ResponseEntity.ok(productResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
//    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//        productService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
//}
