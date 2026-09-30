package com.jody.smartpantry.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class IngredientValidator {

    public static final String EXPIRY_DATE_PATTERN = "yyyy-MM-dd";

    private IngredientValidator() {
    }

    public static ValidationResult validate(String name, String quantityText, String unit, String expiryText) {
        return new ValidationResult(
                validateName(name),
                validateQuantity(quantityText),
                validateUnit(unit),
                validateExpiry(expiryText));
    }

    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Ingredient name cannot be blank";
        }
        return null;
    }

    private static String validateQuantity(String quantityText) {
        if (quantityText == null || quantityText.trim().isEmpty()) {
            return "Quantity must be a number greater than zero";
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText.trim());
        } catch (NumberFormatException e) {
            return "Quantity must be a number greater than zero";
        }
        if (quantity <= 0) {
            return "Quantity must be a number greater than zero";
        }
        return null;
    }

    private static String validateUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return "Please select a unit";
        }
        return null;
    }

    private static String validateExpiry(String expiryText) {
        if (expiryText == null || expiryText.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(EXPIRY_DATE_PATTERN, Locale.US);
        format.setLenient(false);
        try {
            format.parse(expiryText.trim());
        } catch (ParseException e) {
            return "Enter a valid date";
        }
        return null;
    }

    public static final class ValidationResult {
        private final String nameError;
        private final String quantityError;
        private final String unitError;
        private final String expiryError;

        ValidationResult(String nameError, String quantityError, String unitError, String expiryError) {
            this.nameError = nameError;
            this.quantityError = quantityError;
            this.unitError = unitError;
            this.expiryError = expiryError;
        }

        public boolean isValid() {
            return nameError == null && quantityError == null && unitError == null && expiryError == null;
        }

        public String getNameError() {
            return nameError;
        }

        public String getQuantityError() {
            return quantityError;
        }

        public String getUnitError() {
            return unitError;
        }

        public String getExpiryError() {
            return expiryError;
        }
    }
}
