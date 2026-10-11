package com.example.smartpantry.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class InputValidator {

    public static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "item"};

    public static final int MAX_NAME_LENGTH = 40;
    public static final double MAX_QUANTITY = 1_000_000;

    private static final Pattern NUMBER = Pattern.compile("^(\\d+(\\.\\d+)?|\\.\\d+)$");
    private static final Pattern DATE_FORMAT = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private InputValidator() {
    }

    public static String cleanName(String raw) {
        return raw == null ? "" : raw.trim().replaceAll("\\s+", " ");
    }

    public static String validateName(String raw) {
        String name = cleanName(raw);
        if (name.isEmpty()) {
            return "Enter an ingredient name";
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return "Name must be " + MAX_NAME_LENGTH + " characters or fewer";
        }
        if (!name.matches(".*\\p{L}.*")) {
            return "Name must contain letters";
        }
        return null;
    }

    public static String validateQuantity(String raw) {
        String text = prepareQuantity(raw);
        if (text.isEmpty()) {
            return "Enter a quantity";
        }
        if (!NUMBER.matcher(text).matches()) {
            return "Quantity must be a number, for example 2 or 0.5";
        }
        double value = Double.parseDouble(text);
        if (value <= 0) {
            return "Quantity must be greater than zero";
        }
        if (value > MAX_QUANTITY) {
            return "Quantity is too large";
        }
        return null;
    }

    public static double parseQuantity(String raw) {
        return Double.parseDouble(prepareQuantity(raw));
    }

    public static String validateUnit(String raw) {
        if (raw != null) {
            for (String unit : UNITS) {
                if (unit.equals(raw.trim())) {
                    return null;
                }
            }
        }
        return "Select a unit";
    }

    public static String validateExpiry(String raw) {
        String text = normalizeExpiry(raw);
        if (text == null) {
            return null;
        }
        if (!DATE_FORMAT.matcher(text).matches()) {
            return "Use the format yyyy-MM-dd, for example 2026-09-30";
        }
        LocalDate date;
        try {
            date = LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return "That is not a real calendar date";
        }
        if (date.getYear() < 2000 || date.getYear() > 2100) {
            return "Enter a year between 2000 and 2100";
        }
        return null;
    }

    public static String normalizeExpiry(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        return text.isEmpty() ? null : text;
    }

    private static String prepareQuantity(String raw) {
        String text = raw == null ? "" : raw.trim();
        if (text.indexOf('.') < 0 && text.indexOf(',') >= 0 && text.indexOf(',') == text.lastIndexOf(',')) {
            text = text.replace(',', '.');
        }
        return text;
    }
}
