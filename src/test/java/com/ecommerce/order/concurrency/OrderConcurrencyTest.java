package com.ecommerce.order.concurrency;

import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.RoleName;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.common.exception.OrderCancellationException;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.email.EmailService;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.verify;

/**
 * Fires real parallel requests at the real service, real JPA and a real
 * (in-memory) database to prove the order flow is safe under concurrency.
 *
 * Kafka is disabled, so the order notification goes straight to EmailService,
 * which is mocked so we can count how many confirmations were sent.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:oms_concurrency;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cache.type=none",
        "app.kafka.enabled=false",
        "app.mail.from=test@example.com",
        "spring.mail.username=test",
        "spring.mail.password=test",
        "jwt.secret=test-secret-test-secret-test-secret-123456",
        "jwt.expiration=86400000"
})
class OrderConcurrencyTest {

    private static final AtomicLong SEQUENCE = new AtomicLong(1_000_000_000L);

    @Autowired private OrderService orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private OrderRepository orderRepository;

    @MockitoBean private EmailService emailService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void lastUnitInStock_isSoldToExactlyOneBuyer() throws Exception {
        Product product = createProduct(1);
        List<User> buyers = createBuyersWithCart(product, 10, 1);

        List<Throwable> results = runInParallel(
                buyers.stream().map(u -> placeOrderAs(u)).toList());

        assertEquals(1, results.stream().filter(r -> r == null).count(),
                "exactly one buyer may get the last unit");
        results.stream().filter(r -> r != null).forEach(r ->
                assertInstanceOf(InsufficientStockException.class, r));

        assertEquals(0, stockOf(product), "stock must hit zero, never go negative");
        assertEquals(1, countOrders(buyers));

        // one successful order -> exactly one confirmation, none for the losers
        verify(emailService, after(1500).times(1)).sendOrderConfirmation(any(OrderPlacedEvent.class));
    }

    @Test
    void stockOfFive_isSharedAcrossTwentyBuyers() throws Exception {
        Product product = createProduct(5);
        List<User> buyers = createBuyersWithCart(product, 20, 1);

        List<Throwable> results = runInParallel(
                buyers.stream().map(u -> placeOrderAs(u)).toList());

        assertEquals(5, results.stream().filter(r -> r == null).count());
        assertEquals(0, stockOf(product));
        assertEquals(5, countOrders(buyers));
    }

    @Test
    void cancellingTheSameOrderTwiceAtOnce_restoresStockOnlyOnce() throws Exception {
        Product product = createProduct(10);
        User buyer = createBuyersWithCart(product, 1, 3).get(0);

        asUser(buyer);
        OrderResponse placed = orderService.placeOrder();
        SecurityContextHolder.clearContext();
        assertEquals(7, stockOf(product));

        List<Callable<Throwable>> cancels = List.of(
                cancelAs(buyer, placed.getOrderId()),
                cancelAs(buyer, placed.getOrderId()));
        List<Throwable> results = runInParallel(cancels);

        assertEquals(1, results.stream().filter(r -> r == null).count(),
                "one cancel wins");
        results.stream().filter(r -> r != null).forEach(r ->
                assertInstanceOf(OrderCancellationException.class, r));

        assertEquals(10, stockOf(product),
                "stock must be restored once (10), not twice (13)");
        assertEquals(OrderStatus.CANCELLED,
                orderRepository.findById(placed.getOrderId()).orElseThrow().getStatus());
    }

    // ---------------------------------------------------------------- helpers

    private Callable<Throwable> placeOrderAs(User user) {
        return () -> {
            asUser(user);
            try {
                orderService.placeOrder();
                return null;
            } catch (Throwable t) {
                return t;
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    }

    private Callable<Throwable> cancelAs(User user, Long orderId) {
        return () -> {
            asUser(user);
            try {
                orderService.cancelOrder(orderId);
                return null;
            } catch (Throwable t) {
                return t;
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    }

    /** Releases all tasks at the same instant; a null result means the task succeeded. */
    private List<Throwable> runInParallel(List<Callable<Throwable>> tasks) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(tasks.size());
        CountDownLatch startGate = new CountDownLatch(1);
        try {
            List<Future<Throwable>> futures = new ArrayList<>();
            for (Callable<Throwable> task : tasks) {
                futures.add(pool.submit(() -> {
                    startGate.await();
                    return task.call();
                }));
            }
            startGate.countDown();

            List<Throwable> results = new ArrayList<>();
            for (Future<Throwable> future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
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

    private List<User> createBuyersWithCart(Product product, int count, int quantity) {
        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER).orElseThrow();
        List<User> buyers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
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
            buyers.add(user);
        }
        return buyers;
    }

    private void asUser(User user) {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))));
    }

    private int stockOf(Product product) {
        return productRepository.findById(product.getId()).orElseThrow().getStockQuantity();
    }

    private int countOrders(List<User> users) {
        return users.stream().mapToInt(u -> orderRepository.findByUser(u).size()).sum();
    }
}
