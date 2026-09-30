package com.jody.smartpantry.ui;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.SettingsRepository;

public class SettingsActivity extends BaseActivity {

    private SettingsRepository settingsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsRepository = new SettingsRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation(bottomNavigation, R.id.nav_settings);

        SwitchMaterial switchExpiryAlerts = findViewById(R.id.switch_expiry_alerts);
        SwitchMaterial switchMetricUnits = findViewById(R.id.switch_metric_units);

        switchExpiryAlerts.setChecked(settingsRepository.isExpiryAlertsEnabled());
        switchMetricUnits.setChecked(settingsRepository.isMetricUnitsPreferred());

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsRepository.setExpiryAlertsEnabled(isChecked));
        switchMetricUnits.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsRepository.setMetricUnitsPreferred(isChecked));
    }
}

