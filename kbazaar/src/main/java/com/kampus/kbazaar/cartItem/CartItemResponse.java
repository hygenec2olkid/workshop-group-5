package com.kampus.kbazaar.cartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        String productName,
        String productSku,
        Integer quantity,
        BigDecimal subTotal,
        BigDecimal discount,
        String promotionCode,
        Integer freeProduct) {}
