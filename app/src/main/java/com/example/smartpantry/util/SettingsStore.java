package com.example.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStore {

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_EXPIRY_DAYS = "expiry_warning_days";

    private final SharedPreferences preferences;

    public SettingsStore(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public int getExpiryWarningDays() {
        return ExpiryHelper.sanitizeWarningDays(
                preferences.getInt(KEY_EXPIRY_DAYS, ExpiryHelper.DEFAULT_WARNING_DAYS));
    }

    public void setExpiryWarningDays(int days) {
        preferences.edit().putInt(KEY_EXPIRY_DAYS, ExpiryHelper.sanitizeWarningDays(days)).apply();
    }
}
