package com.kampus.kbazaar.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class BigDecimalPercentagesTest {
    private final BigDecimalPercentages bigDecimalPercentages = new BigDecimalPercentages();

    @Test
    void shouldReturnValueOfPercent() {
        BigDecimal percentage = BigDecimal.valueOf(50);
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal actual = bigDecimalPercentages.percentOf(percentage, total);

        BigDecimal expected = new BigDecimal("50.00");
        assertEquals(expected, actual);
    }
}
