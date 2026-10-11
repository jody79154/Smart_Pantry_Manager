package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class InputValidatorTest {

    @Test
    public void blankNameIsRejected() {
        assertNotNull(InputValidator.validateName(""));
        assertNotNull(InputValidator.validateName("    "));
        assertNotNull(InputValidator.validateName(null));
    }

    @Test
    public void validNameIsAccepted() {
        assertNull(InputValidator.validateName("Tomato"));
        assertNull(InputValidator.validateName("  olive oil "));
    }

    @Test
    public void nameWhitespaceIsTrimmedAndCollapsed() {
        assertEquals("olive oil", InputValidator.cleanName("   olive    oil  "));
    }

    @Test
    public void nameWithoutLettersOrTooLongIsRejected() {
        assertNotNull(InputValidator.validateName("12345"));
        assertNotNull(InputValidator.validateName("a".repeat(41)));
        assertNull(InputValidator.validateName("a".repeat(40)));
    }

    @Test
    public void blankOrNonNumericQuantityIsRejected() {
        assertNotNull(InputValidator.validateQuantity(""));
        assertNotNull(InputValidator.validateQuantity("   "));
        assertNotNull(InputValidator.validateQuantity(null));
        assertNotNull(InputValidator.validateQuantity("abc"));
        assertNotNull(InputValidator.validateQuantity("1.2.3"));
        assertNotNull(InputValidator.validateQuantity("1e5"));
        assertNotNull(InputValidator.validateQuantity("NaN"));
        assertNotNull(InputValidator.validateQuantity("Infinity"));
    }

    @Test
    public void zeroAndNegativeQuantitiesAreRejected() {
        assertNotNull(InputValidator.validateQuantity("0"));
        assertNotNull(InputValidator.validateQuantity("0.0"));
        assertNotNull(InputValidator.validateQuantity("-1"));
    }

    @Test
    public void oversizedQuantityIsRejected() {
        assertNotNull(InputValidator.validateQuantity("1000001"));
        assertNull(InputValidator.validateQuantity("1000000"));
    }

    @Test
    public void wholeDecimalAndPaddedQuantitiesAreAccepted() {
        assertNull(InputValidator.validateQuantity("2"));
        assertNull(InputValidator.validateQuantity("1.5"));
        assertNull(InputValidator.validateQuantity(".5"));
        assertNull(InputValidator.validateQuantity("  3  "));
    }

    @Test
    public void commaDecimalSeparatorIsAccepted() {
        assertNull(InputValidator.validateQuantity("1,5"));
        assertEquals(1.5, InputValidator.parseQuantity("1,5"), 1e-9);
        assertEquals(0.25, InputValidator.parseQuantity(" 0.25 "), 1e-9);
    }

    @Test
    public void unitMustBeSelectedFromKnownUnits() {
        assertNotNull(InputValidator.validateUnit(""));
        assertNotNull(InputValidator.validateUnit(null));
        assertNotNull(InputValidator.validateUnit("bananas"));
        for (String unit : InputValidator.UNITS) {
            assertNull(InputValidator.validateUnit(unit));
        }
    }

    @Test
    public void blankExpiryIsOptional() {
        assertNull(InputValidator.validateExpiry(null));
        assertNull(InputValidator.validateExpiry(""));
        assertNull(InputValidator.validateExpiry("   "));
        assertNull(InputValidator.normalizeExpiry("   "));
    }

    @Test
    public void realCalendarDatesAreAccepted() {
        assertNull(InputValidator.validateExpiry("2026-09-30"));
        assertNull(InputValidator.validateExpiry(" 2026-09-30 "));
        assertNull(InputValidator.validateExpiry("2028-02-29"));
        assertEquals("2026-09-30", InputValidator.normalizeExpiry(" 2026-09-30 "));
    }

    @Test
    public void invalidExpiryDatesAreRejected() {
        assertNotNull(InputValidator.validateExpiry("2026-02-30"));
        assertNotNull(InputValidator.validateExpiry("2027-02-29"));
        assertNotNull(InputValidator.validateExpiry("2026-13-01"));
        assertNotNull(InputValidator.validateExpiry("30-09-2026"));
        assertNotNull(InputValidator.validateExpiry("abc"));
        assertNotNull(InputValidator.validateExpiry("1999-12-31"));
        assertNotNull(InputValidator.validateExpiry("2101-01-01"));
    }
}
