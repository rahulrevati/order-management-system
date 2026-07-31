package com.ecommerce.cart.repository;

import com.ecommerce.auth.entity.User;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUser(User user);

    boolean existsByUserAndProduct(User user, Product product);
}