//package com.ecommerce.product.service;
//
//import com.ecommerce.category.entity.Category;
//import com.ecommerce.category.repository.CategoryRepository;
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import com.ecommerce.product.dto.ProductRequest;
//import com.ecommerce.product.dto.ProductResponse;
//import com.ecommerce.product.entity.Product;
//import com.ecommerce.product.mapper.ProductMapper;
//import com.ecommerce.product.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProductServiceImpl implements ProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//
//    @Override
//    public ProductResponse createProduct(ProductRequest productRequest) {
//        Category category = categoryRepository.findById(productRequest.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));
//
//        Product product = ProductMapper.toEntity(productRequest);
//        product.setCategory(category);
//        Product savedProduct = productRepository.save(product);
//        return ProductMapper.toResponse(savedProduct);
//    }
//
//    @Override
//    public ProductResponse getProductById(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
//        return ProductMapper.toResponse(product);
//    }
//
//    @Override
//    public List<ProductResponse> getAllProducts() {
//        List<Product> products = productRepository.findAll();
//        return products.stream()
//                .map(ProductMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public List<ProductResponse> getProductsByCategory(Long categoryId) {
//        List<Product> products = productRepository.findByCategoryId(categoryId);
//        return products.stream()
//                .map(ProductMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
//
//        Category category = categoryRepository.findById(productRequest.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));
//
//        product.setName(productRequest.getName());
//        product.setDescription(productRequest.getDescription());
//        product.setPrice(productRequest.getPrice());
//        product.setStockQuantity(productRequest.getStockQuantity());
//        product.setCategory(category);
//
//        Product updatedProduct = productRepository.save(product);
//        return ProductMapper.toResponse(updatedProduct);
//    }
//
//    @Override
//    public void deleteProduct(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
//        productRepository.delete(product);
//    }
//}
