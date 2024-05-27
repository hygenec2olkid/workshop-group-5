package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.cartItem.CartItemResponse;
import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        String userName,
        List<CartItemResponse> products,
        String promotionCode,
        BigDecimal discount,
        BigDecimal totalDiscount,
        BigDecimal total,
        BigDecimal finalTotal,
        Integer fee) {}
