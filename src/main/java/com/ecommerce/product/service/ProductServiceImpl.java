package com.ecommerce.product.service;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.ResourceAlreadyExistsException;
import com.ecommerce.common.exception.ResourceNotFoundException;

import com.ecommerce.product.dto.request.CreateProductRequest;
import com.ecommerce.product.dto.request.UpdateProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.dto.response.BulkProductImportResponse;
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

import java.util.*;
import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;

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
    public List<ProductResponse> getAllProductsForAdmin() {

        log.info("Fetching all products for admin, including inactive products");

        return productRepository.findAllByOrderByIdAsc()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @CacheEvict(value = "products", key = "'all'")
    @Override
    public BulkProductImportResponse importProductsFromCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        try {
            String content = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            List<String> rows = content.lines().toList();
            if (rows.isEmpty()) throw new IllegalArgumentException("CSV file is empty");

            List<String> header = parseCsvLine(rows.get(0));
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < header.size(); i++) columns.put(header.get(i).trim().toLowerCase(Locale.ROOT), i);
            String[] required = {"name", "description", "sku", "price", "stockquantity", "imageurl", "categoryid"};
            for (String column : required) if (!columns.containsKey(column)) throw new IllegalArgumentException("Missing CSV column: " + column);

            List<String> errors = new ArrayList<>();
            List<Product> products = new ArrayList<>();
            Set<String> csvSkus = new HashSet<>();

            for (int rowNumber = 2; rowNumber <= rows.size(); rowNumber++) {
                String raw = rows.get(rowNumber - 1);
                if (raw.trim().isEmpty()) continue;
                try {
                    List<String> values = parseCsvLine(raw);
                    String name = value(values, columns, "name");
                    String description = value(values, columns, "description");
                    String sku = value(values, columns, "sku");
                    String priceText = value(values, columns, "price");
                    String stockText = value(values, columns, "stockquantity");
                    String imageUrl = value(values, columns, "imageurl");
                    String categoryText = value(values, columns, "categoryid");

                    if (name.isBlank()) throw new IllegalArgumentException("name is required");
                    if (sku.isBlank()) throw new IllegalArgumentException("sku is required");
                    if (!csvSkus.add(sku.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("duplicate SKU in CSV");
                    if (productRepository.existsBySku(sku)) throw new IllegalArgumentException("SKU already exists: " + sku);

                    BigDecimal price = new BigDecimal(priceText);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("price must be greater than 0");
                    int stock = Integer.parseInt(stockText);
                    if (stock < 0) throw new IllegalArgumentException("stockQuantity cannot be negative");
                    Long categoryId = Long.parseLong(categoryText);
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException("category not found: " + categoryId));

                    if (name.length() > 150) throw new IllegalArgumentException("name exceeds 150 characters");
                    if (description.length() > 1000) throw new IllegalArgumentException("description exceeds 1000 characters");
                    if (sku.length() > 50) throw new IllegalArgumentException("sku exceeds 50 characters");
                    if (imageUrl.length() > 500) throw new IllegalArgumentException("imageUrl exceeds 500 characters");

                    products.add(Product.builder().name(name).description(description).sku(sku).price(price)
                            .stockQuantity(stock).imageUrl(imageUrl).active(true).category(category).build());
                } catch (Exception ex) {
                    errors.add("Row " + rowNumber + ": " + ex.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                return BulkProductImportResponse.builder().importedCount(0).rejectedCount(errors.size()).errors(errors).build();
            }

            if (!products.isEmpty()) productRepository.saveAll(products);
            log.info("Bulk product import completed: {} products", products.size());
            return BulkProductImportResponse.builder().importedCount(products.size()).rejectedCount(0).errors(List.of()).build();
        } catch (java.io.IOException ex) {
            throw new IllegalArgumentException("Unable to read CSV file", ex);
        }
    }

    private String value(List<String> values, Map<String,Integer> columns, String key) {
        Integer index = columns.get(key);
        return index != null && index < values.size() ? values.get(index).trim() : "";
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { current.append('"'); i++; }
                else quoted = !quoted;
            } else if (ch == ',' && !quoted) { values.add(current.toString()); current.setLength(0); }
            else current.append(ch);
        }
        values.add(current.toString());
        return values;
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
