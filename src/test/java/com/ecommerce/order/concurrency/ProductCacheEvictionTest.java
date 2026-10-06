package com.ecommerce.order.concurrency;

import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.enums.RoleName;
import com.ecommerce.email.EmailService;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Product responses are cached (they include stockQuantity). Placing or
 * cancelling an order changes stock, so the cache must not keep serving the old
 * number. This runs the real Spring cache (in-memory) against a real
 * transaction to prove the eviction happens after commit.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:oms_cache;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cache.type=simple",
        "app.kafka.enabled=false",
        "app.mail.from=test@example.com",
        "spring.mail.username=test",
        "spring.mail.password=test",
        "jwt.secret=test-secret-test-secret-test-secret-123456",
        "jwt.expiration=86400000"
})
class ProductCacheEvictionTest {

    private static final AtomicLong SEQUENCE = new AtomicLong(2_000_000_000L);

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CartRepository cartRepository;

    @MockitoBean private EmailService emailService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void placingAnOrder_refreshesCachedStock() {
        Product product = createProduct(5);
        User buyer = createBuyerWithCart(product, 2);

        // fill both caches with the current stock (5)
        assertEquals(5, cachedStockById(product));
        assertEquals(5, cachedStockInList(product));

        asUser(buyer);
        orderService.placeOrder();
        SecurityContextHolder.clearContext();

        assertEquals(3, cachedStockById(product), "single-product cache must show the new stock");
        assertEquals(3, cachedStockInList(product), "product-list cache must show the new stock");
    }

    @Test
    void cancellingAnOrder_refreshesCachedStock() {
        Product product = createProduct(5);
        User buyer = createBuyerWithCart(product, 2);

        asUser(buyer);
        OrderResponse placed = orderService.placeOrder();
        SecurityContextHolder.clearContext();

        // fill both caches with the post-order stock (3)
        assertEquals(3, cachedStockById(product));
        assertEquals(3, cachedStockInList(product));

        asUser(buyer);
        orderService.cancelOrder(placed.getOrderId());
        SecurityContextHolder.clearContext();

        assertEquals(5, cachedStockById(product), "single-product cache must show the restored stock");
        assertEquals(5, cachedStockInList(product), "product-list cache must show the restored stock");
    }

    // ---------------------------------------------------------------- helpers

    private int cachedStockById(Product product) {
        return productService.getProductById(product.getId()).getStockQuantity();
    }

    private int cachedStockInList(Product product) {
        return productService.getAllProducts().stream()
                .filter(p -> p.getId().equals(product.getId()))
                .map(ProductResponse::getStockQuantity)
                .findFirst()
                .orElseThrow();
    }

    private Product createProduct(int stock) {
        long id = SEQUENCE.incrementAndGet();
        Category category = categoryRepository.save(Category.builder()
                .name("category-" + id)
                .description("test")
                .build());
        return productRepository.save(Product.builder()
                .name("Widget " + id)
                .sku("SKU-" + id)
                .price(new BigDecimal("10.00"))
                .stockQuantity(stock)
                .category(category)
                .build());
    }

    private User createBuyerWithCart(Product product, int quantity) {
        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER).orElseThrow();
        long id = SEQUENCE.incrementAndGet();
        User user = userRepository.save(User.builder()
                .firstName("Test")
                .lastName("Buyer")
                .email("buyer-" + id + "@example.com")
                .password("not-used")
                .phoneNumber(String.valueOf(id))
                .role(customerRole)
                .build());
        BigDecimal unitPrice = product.getPrice();
        cartRepository.save(CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build());
        return user;
    }

    private void asUser(User user) {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))));
    }
}
