package com.example.smartpantry.ui;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.util.SettingsStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SettingsStore settings;
    private BottomNavigationView bottomNav;
    private RadioGroup radioWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new SettingsStore(this);
        bottomNav = findViewById(R.id.bottomNav);
        radioWindow = findViewById(R.id.radioWindow);
        SwitchMaterial switchAlerts = findViewById(R.id.switchExpiryAlerts);

        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_settings);

        boolean alertsEnabled = settings.isExpiryAlertsEnabled();
        switchAlerts.setChecked(alertsEnabled);
        radioWindow.check(radioIdForDays(settings.getExpiryWarningDays()));
        setWindowEnabled(alertsEnabled);

        // Expiry alerts switch
        switchAlerts.setOnCheckedChangeListener((button, checked) -> {
            settings.setExpiryAlertsEnabled(checked);
            setWindowEnabled(checked);
        });

        // Warning window
        radioWindow.setOnCheckedChangeListener((group, checkedId) ->
                settings.setExpiryWarningDays(daysForRadioId(checkedId)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }

    private void setWindowEnabled(boolean enabled) {
        for (int i = 0; i < radioWindow.getChildCount(); i++) {
            radioWindow.getChildAt(i).setEnabled(enabled);
        }
    }

    private int radioIdForDays(int days) {
        if (days == 5) {
            return R.id.radioDays5;
        }
        return days == 7 ? R.id.radioDays7 : R.id.radioDays3;
    }

    private int daysForRadioId(int radioId) {
        if (radioId == R.id.radioDays5) {
            return 5;
        }
        return radioId == R.id.radioDays7 ? 7 : 3;
    }
}
