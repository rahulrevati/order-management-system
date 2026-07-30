//package com.ecommerce.cart.service;
//
//import com.ecommerce.auth.entity.User;
//import com.ecommerce.auth.repository.UserRepository;
//import com.ecommerce.cart.dto.CartRequest;
//import com.ecommerce.cart.dto.CartResponse;
//import com.ecommerce.cart.entity.Cart;
//import com.ecommerce.cart.entity.CartItem;
//import com.ecommerce.cart.mapper.CartMapper;
//import com.ecommerce.cart.repository.CartRepository;
//import com.ecommerce.cart.repository.CartItemRepository;
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import com.ecommerce.product.entity.Product;
//import com.ecommerce.product.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CartServiceImpl implements CartService {
//
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//
//    @Override
//    @Transactional
//    public CartResponse addToCart(CartRequest cartRequest, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Product product = productRepository.findById(cartRequest.getProductId())
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartRequest.getProductId()));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseGet(() -> {
//                    Cart newCart = Cart.builder().user(user).build();
//                    return cartRepository.save(newCart);
//                });
//
//        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
//
//////        if (existingItem != null) {
//////            existingItem.setQuantity(existingItem.getQuantity() + cartRequest.getQuantity());
//////            cartItemRepository.save(existingItem);
////        } else {
////            CartItem cartItem = CartItem.builder()
////                    .cart(cart)
////                    .product(product)
////                    .quantity(cartRequest.getQuantity())
////                    .price(product.getPrice())
////                    .build();
////            cartItemRepository.save(cartItem);
////        }
//
//        return CartMapper.toResponse(cartRepository.findByUser(user).orElse(cart));
//    }
//
//    @Override
//    public CartResponse getCart(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", userEmail));
//
//        return CartMapper.toResponse(cart);
//    }
//
//    @Override
//    @Transactional
//    public CartResponse updateCartItem(Long cartItemId, Integer quantity, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", userEmail));
//
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
//
//        if (!cartItem.getCart().getId().equals(cart.getId())) {
//            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
//        }
//
//        cartItem.setQuantity(quantity);
//        cartItemRepository.save(cartItem);
//
//        return CartMapper.toResponse(cart);
//    }
//
//    @Override
//    @Transactional
//    public CartResponse removeCartItem(Long cartItemId, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", userEmail));
//
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
//
//        if (!cartItem.getCart().getId().equals(cart.getId())) {
//            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
//        }
//
//        cartItemRepository.delete(cartItem);
//
//        return CartMapper.toResponse(cart);
//    }
//
//    @Override
//    @Transactional
//    public void clearCart(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", userEmail));
//
//        cartItemRepository.deleteByCart(cart);
//    }
//}
