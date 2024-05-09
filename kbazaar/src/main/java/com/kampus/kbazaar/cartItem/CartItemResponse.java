package com.kampus.kbazaar.cartItem;

import java.math.BigDecimal;

public record CartItemResponse(String productName, Integer quantity, BigDecimal subTotal) {}
