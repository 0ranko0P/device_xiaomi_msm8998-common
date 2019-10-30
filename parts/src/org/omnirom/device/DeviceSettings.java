/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceSettings extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_CATEGORY_DISPLAY = "display";
    public static final String KEY_TAPTOWAKE_SWITCH = "taptowake";
    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_BTN_BRIGHTNESS = "btn_brightness";

    private static final String KEY_DEVICE_DOZE = "device_doze";
    private static final String KEY_DEVICE_DOZE_PACKAGE_NAME = "org.lineageos.settings.doze";

    private static final String KEY_KCAL = "kcal";

    static final String KEY_SPECTRUM = "spectrum";
    static final String KEY_SPECTRUM_SYSTEM_PROPERTY = "persist.sys.spectrum.profile";

    private TwoStatePreference mTapToWakeSwitch;
    private VibratorStrengthPreference mVibratorStrength;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.main);

        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
        lv.setDividerHeight(0);

	ListPreference spectrumPreference = (ListPreference) findPreference(KEY_SPECTRUM);
        spectrumPreference.setValue(SystemProperties.get(KEY_SPECTRUM_SYSTEM_PROPERTY, "0"));
        spectrumPreference.setOnPreferenceChangeListener(this);

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        mTapToWakeSwitch = (TwoStatePreference) findPreference(KEY_TAPTOWAKE_SWITCH);
        mTapToWakeSwitch.setEnabled(TapToWakeSwitch.isSupported());
        mTapToWakeSwitch.setChecked(TapToWakeSwitch.isCurrentlyEnabled(this));
        mTapToWakeSwitch.setOnPreferenceChangeListener(new TapToWakeSwitch());

        if (!isAppInstalled(KEY_DEVICE_DOZE_PACKAGE_NAME)) {
            PreferenceCategory displayCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_DISPLAY);
            displayCategory.removePreference(findPreference(KEY_DEVICE_DOZE));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	if (KEY_KCAL.equals(preference.getKey())) {
	    Intent intent = new Intent(this, DisplayCalibration.class);
	    startActivity(intent);
	    return true;
	}
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	if (KEY_SPECTRUM.equals(preference.getKey())) {
	    // If you did not handle selinux stuff, setprop will thow an exception
            SystemProperties.set(KEY_SPECTRUM_SYSTEM_PROPERTY, newValue.toString());
            return true;
        }
	return false;
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
