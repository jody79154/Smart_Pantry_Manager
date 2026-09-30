package com.jody.smartpantry.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsRepository {

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_METRIC_UNITS = "prefer_metric_units";

    private final SharedPreferences preferences;

    public SettingsRepository(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public boolean isMetricUnitsPreferred() {
        return preferences.getBoolean(KEY_METRIC_UNITS, true);
    }

    public void setMetricUnitsPreferred(boolean preferred) {
        preferences.edit().putBoolean(KEY_METRIC_UNITS, preferred).apply();
    }
}
