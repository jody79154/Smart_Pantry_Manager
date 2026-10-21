package com.example.smartpantry.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class QuantityFormatter {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);

    private QuantityFormatter() {
    }

    public static String format(double quantity) {
        return new DecimalFormat("0.######", SYMBOLS).format(quantity);
    }
}
