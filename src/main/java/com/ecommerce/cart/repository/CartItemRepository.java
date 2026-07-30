//package com.ecommerce.cart.repository;
//
//import com.ecommerce.cart.entity.Cart;
//import com.ecommerce.cart.entity.CartItem;
//import com.ecommerce.product.entity.Product;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//
//    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
//
//    void deleteByCart(Cart cart);
//}
