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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private CartItem cartItem;
    private AddToCartRequest addToCartRequest;
    private UpdateCartRequest updateCartRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setActive(true);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("999.99"));
        cartItem.setTotalPrice(new BigDecimal("1999.98"));

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(1L);
        addToCartRequest.setQuantity(2);

        updateCartRequest = new UpdateCartRequest();
        updateCartRequest.setQuantity(3);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void addToCart_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProduct(any(User.class), any(Product.class))).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));
        when(cartMapper.toResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartResponse response = cartService.addToCart(addToCartRequest);

        assertNotNull(response);
        verify(cartRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_ProductInactive() {
        product.setActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertThrows(ProductInactiveException.class, () -> cartService.addToCart(addToCartRequest));

        verify(cartRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_InsufficientStock() {
        product.setStockQuantity(1);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> cartService.addToCart(addToCartRequest));

        verify(cartRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_UpdateExistingItem() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProduct(any(User.class), any(Product.class))).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));
        when(cartMapper.toResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartResponse response = cartService.addToCart(addToCartRequest);

        assertNotNull(response);
        verify(cartRepository).save(any(CartItem.class));
    }

    @Test
    void getCart_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));
        when(cartMapper.toResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartResponse response = cartService.getCart();

        assertNotNull(response);
        verify(cartRepository).findByUser(any(User.class));
    }

    @Test
    void updateCartItem_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(cartItem));
        when(cartMapper.toResponse(any(CartItem.class))).thenReturn(new CartItemResponse());

        CartResponse response = cartService.updateCartItem(1L, updateCartRequest);

        assertNotNull(response);
        verify(cartRepository).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_InsufficientStock() {
        product.setStockQuantity(2);
        updateCartRequest.setQuantity(5);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));

        assertThrows(InsufficientStockException.class, () -> cartService.updateCartItem(1L, updateCartRequest));

        verify(cartRepository, never()).save(any(CartItem.class));
    }

    @Test
    void removeCartItem_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));

        cartService.removeCartItem(1L);

        verify(cartRepository).delete(any(CartItem.class));
    }

    @Test
    void removeCartItem_UnauthorizedAccess() {
        User otherUser = new User();
        otherUser.setId(2L);
        cartItem.setUser(otherUser);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartItem));

        assertThrows(UnauthorizedCartAccessException.class, () -> cartService.removeCartItem(1L));

        verify(cartRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void clearCart_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        cartService.clearCart();

        verify(cartRepository).deleteByUser(any(User.class));
    }
}
