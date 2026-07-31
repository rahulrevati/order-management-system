package com.ecommerce.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {

    private List<CartItemResponse> items;

    private BigDecimal grandTotal;
}