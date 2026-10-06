package com.ecommerce.order.repository;

import com.ecommerce.auth.entity.User;
import com.ecommerce.order.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUser(User user);

    /**
     * Loads the order with a row lock (SELECT ... FOR UPDATE) so that two
     * concurrent cancel / status-change requests cannot both act on the same
     * state, for example restoring the stock twice.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
