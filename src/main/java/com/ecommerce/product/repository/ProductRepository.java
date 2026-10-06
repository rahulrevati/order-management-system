package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByActiveTrue();

    List<Product> findAllByOrderByIdAsc();

    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * Atomically takes {@code quantity} units out of stock.
     *
     * The stock check and the decrement happen in a single UPDATE statement, so
     * the database serialises concurrent buyers on the product row. Two orders
     * can never both take the last unit.
     *
     * @return 1 if the stock was reserved, 0 if the product is inactive or has
     *         less than {@code quantity} units left
     */
    @Modifying
    @Query("""
            UPDATE Product p
               SET p.stockQuantity = p.stockQuantity - :quantity
             WHERE p.id = :productId
               AND p.active = true
               AND p.stockQuantity >= :quantity
            """)
    int decreaseStockIfAvailable(@Param("productId") Long productId,
                                 @Param("quantity") int quantity);

    /** Atomically puts {@code quantity} units back into stock (order cancelled). */
    @Modifying
    @Query("""
            UPDATE Product p
               SET p.stockQuantity = p.stockQuantity + :quantity
             WHERE p.id = :productId
            """)
    int increaseStock(@Param("productId") Long productId,
                      @Param("quantity") int quantity);
}
