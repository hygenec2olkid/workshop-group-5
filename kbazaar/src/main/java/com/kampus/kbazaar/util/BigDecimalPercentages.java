package com.kampus.kbazaar.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalPercentages {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public BigDecimal percentOf(BigDecimal percentage, BigDecimal total) {
        return percentage.multiply(total).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
