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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setSku("SKU-001");
        product.setDescription("High-end laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setActive(true);
        product.setCategory(category);

        createRequest = new CreateProductRequest();
        createRequest.setName("Laptop");
        createRequest.setSku("SKU-001");
        createRequest.setDescription("High-end laptop");
        createRequest.setPrice(new BigDecimal("999.99"));
        createRequest.setStockQuantity(10);
        createRequest.setCategoryId(1L);

        updateRequest = new UpdateProductRequest();
        updateRequest.setName("Updated Laptop");
        updateRequest.setSku("SKU-002");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("1099.99"));
        updateRequest.setStockQuantity(15);
        updateRequest.setCategoryId(1L);
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productMapper.toEntity(any(CreateProductRequest.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse response = productService.createProduct(createRequest);

        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_SkuAlreadyExists() {
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> productService.createProduct(createRequest));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_CategoryNotFound() {
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(createRequest));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateRequest));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_SkuAlreadyExists() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> productService.updateProduct(1L, updateRequest));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(1L);

        verify(productRepository).save(any(Product.class));
        assertFalse(product.getActive());
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getAllProducts_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByActiveTrue()).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        List<ProductResponse> responses = productService.getAllProducts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void getProductsByCategory_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByCategoryId(anyLong())).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        List<ProductResponse> responses = productService.getProductsByCategory(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository).findByCategoryId(1L);
    }

    @Test
    void searchProducts_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponse());

        List<ProductResponse> responses = productService.searchProducts("Laptop");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository).findByNameContainingIgnoreCase("Laptop");
    }
}
