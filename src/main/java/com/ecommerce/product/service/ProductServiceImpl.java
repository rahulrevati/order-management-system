package com.ecommerce.product.service;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.ResourceAlreadyExistsException;
import com.ecommerce.common.exception.ResourceNotFoundException;

import com.ecommerce.product.dto.request.CreateProductRequest;
import com.ecommerce.product.dto.request.UpdateProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @CacheEvict(value = "products", key = "'all'")
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        log.info("Creating product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            throw new ResourceAlreadyExistsException(
                    "Product already exists with SKU: " + request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products", key = "'all'")
    })
    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {

        log.info("Updating product with ID: {}", id);

        Product product = getProductEntity(id);

        // Check for duplicate SKU only if SKU is changed
        if (!product.getSku().equalsIgnoreCase(request.getSku())
                && productRepository.existsBySku(request.getSku())) {

            throw new ResourceAlreadyExistsException(
                    "Product already exists with SKU: " + request.getSku());
        }

        // Validate category
        Category category = getCategoryEntity(request.getCategoryId());

        // Update product fields
        productMapper.updateProductFromRequest(request, product);

        // Set category explicitly
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with ID: {}", updatedProduct.getId());

        return productMapper.toResponse(updatedProduct);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products", key = "'all'")
    })
    @Override
    public void deleteProduct(Long id) {

        Product product = getProductEntity(id);

        product.setActive(false);

        productRepository.save(product);

        log.info("Product with ID {} marked as inactive", id);
    }
    @Cacheable(value = "products", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        log.info("Fetching product with ID: {}", id);

        return productMapper.toResponse(getProductEntity(id));
    }
    @Cacheable(value = "products", key = "'all'")
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        log.info("Fetching all active products");

        return productRepository.findByActiveTrue()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {

        log.info("Fetching products for category ID: {}", categoryId);

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {

        log.info("Searching products with keyword: {}", keyword);

        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }



    private Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));
    }

    private Category getCategoryEntity(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + categoryId));
    }
}
