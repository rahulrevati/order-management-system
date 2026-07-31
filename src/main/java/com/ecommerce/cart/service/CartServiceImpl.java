package com.ecommerce.cart.service;

import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartItemResponse;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.dto.UpdateCartRequest;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.common.exception.ProductInactiveException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.UnauthorizedCartAccessException;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + productId));
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        User user = getCurrentUser();

        Product product = getProduct(request.getProductId());

        if (!product.getActive()) {
            throw new ProductInactiveException("Product is not active and cannot be added to cart");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Requested quantity exceeds available stock. Available: " + product.getStockQuantity());
        }

        Optional<CartItem> existingCartItem =
                cartRepository.findByUserAndProduct(user, product);

        CartItem cartItem;

        if (existingCartItem.isPresent()) {

            cartItem = existingCartItem.get();

            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        "Requested quantity exceeds available stock. Available: " + product.getStockQuantity());
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem();

            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
        }

        cartItem.setTotalPrice(
                cartItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
        );

        cartRepository.save(cartItem);

        return getCart();
    }
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {

        User user = getCurrentUser();

        List<CartItem> cartItems = cartRepository.findByUser(user);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(cartMapper::toResponse)
                .toList();

        BigDecimal grandTotal = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setItems(itemResponses);
        response.setGrandTotal(grandTotal);

        return response;
    }

    @Override
    public CartResponse updateCartItem(Long cartItemId, UpdateCartRequest request) {

        User user = getCurrentUser();

        CartItem cartItem = getCartItem(cartItemId);

        Product product = cartItem.getProduct();

        if (request.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Requested quantity exceeds available stock. Available: " + product.getStockQuantity());
        }

        cartItem.setQuantity(request.getQuantity());

        cartItem.setTotalPrice(
                cartItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(request.getQuantity()))
        );

        cartRepository.save(cartItem);

        return getCart();
    }

    @Override
    public void removeCartItem(Long cartItemId) {

        User user = getCurrentUser();

        CartItem cartItem = getCartItem(cartItemId);

        cartRepository.delete(cartItem);
    }

    @Override
    public void clearCart() {

        User user = getCurrentUser();

        cartRepository.deleteByUser(user);
    }

    private CartItem getCartItem(Long cartItemId) {

        User user = getCurrentUser();

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedCartAccessException("You can only access your own cart items");
        }

        return cartItem;
    }
}
