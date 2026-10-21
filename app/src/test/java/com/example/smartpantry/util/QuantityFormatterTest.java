package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class QuantityFormatterTest {

    @Test
    public void wholeNumbersHaveNoDecimalPoint() {
        assertEquals("2", QuantityFormatter.format(2));
        assertEquals("2", QuantityFormatter.format(2.0));
        assertEquals("1000", QuantityFormatter.format(1000));
    }

    @Test
    public void fractionsKeepTheirDigitsWithoutTrailingZeros() {
        assertEquals("0.5", QuantityFormatter.format(0.5));
        assertEquals("1.25", QuantityFormatter.format(1.25));
        assertEquals("999999.5", QuantityFormatter.format(999999.5));
    }

    @Test
    public void smallQuantitiesAreNotRoundedToZero() {
        assertEquals("0.0004", QuantityFormatter.format(0.0004));
        assertEquals("0.333333", QuantityFormatter.format(1.0 / 3));
    }

    @Test
    public void formattedQuantitiesPassFormValidationWhenEdited() {
        double[] values = {2, 0.5, 1.25, 0.0004, 1.0 / 3, 999999.5, 1000000};
        for (double value : values) {
            assertNull(String.valueOf(value), InputValidator.validateQuantity(QuantityFormatter.format(value)));
        }
    }
}
