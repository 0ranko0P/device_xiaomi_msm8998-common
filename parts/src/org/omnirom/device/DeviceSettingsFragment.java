/*
 * Copyright (C) 2019 The OmniROM Project
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

import android.os.Bundle;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

import org.omnirom.device.Preference.ButtonSwapPreference;
import org.omnirom.device.Preference.FastChargePreference;
import org.omnirom.device.Preference.S2SVibratorStrengthPreference;
import org.omnirom.device.Preference.SweepToSleepPreference;

import static org.omnirom.device.Preference.ButtonSwapPreference.BUTTONS_SWAP_KEY;
import static org.omnirom.device.Preference.FastChargePreference.USB_FAST_CHARGE_KEY;
import static org.omnirom.device.Preference.S2SVibratorStrengthPreference.KEY_S2S_VIBSTRENGTH;
import static org.omnirom.device.Preference.SweepToSleepPreference.S2S_KEY;

public final class DeviceSettingsFragment extends PreferenceFragment {

    protected static final String TAG = "DeviceSettings";

    private static final String KEY_CATEGORY_HW_BUTTONS = "hw_buttons";
    private static final String KEY_CATEGORY_USB_FASTCHARGE = "usb_fastcharge";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.device_settings, rootKey);

        PreferenceScreen prefSet = getPreferenceScreen();

        ButtonSwapPreference mButtonSwap = (ButtonSwapPreference) prefSet.findPreference(BUTTONS_SWAP_KEY);
        FastChargePreference mFastCharge = (FastChargePreference) findPreference(USB_FAST_CHARGE_KEY);
        SweepToSleepPreference mSweep = (SweepToSleepPreference) findPreference(S2S_KEY);
        S2SVibratorStrengthPreference mVibratorStrengthS2S = (S2SVibratorStrengthPreference) findPreference(KEY_S2S_VIBSTRENGTH);

        mButtonSwap.setEnabled(ButtonSwapPreference.FEATURE.isSupported());
        mFastCharge.setEnabled(FastChargePreference.FEATURE.isSupported());
        mSweep.setEnabled(SweepToSleepPreference.FEATURE.isSupported());
        mVibratorStrengthS2S.setEnabled(S2SVibratorStrengthPreference.FEATURE.isSupported());
    }
}