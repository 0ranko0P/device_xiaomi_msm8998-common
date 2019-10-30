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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SeekBarDialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Backlight Preference used to adjust led brightness of buttons.
 *
 * Created by 0ranko0P <ranko0p@outlook.com> on 2019.10.30
 */
public final class BacklightPreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener {
    private int mOldValue;

    private static final int BACKLIGHT_MIN_BRIGHTNESS = 0;
    private static final int BACKLIGHT_MAX_BRIGHTNESS = 255;
    private static final float PROGRESS_OFFSET = BACKLIGHT_MAX_BRIGHTNESS / 100f;

    private static final String FILE_LED_LEFT  = "/sys/class/leds/button-backlight/brightness";
    private static final String FILE_LED_RIGHT = "/sys/class/leds/button-backlight1/brightness";

    private SeekBar  mSeekBar;
    private TextView mValueText;

    public BacklightPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_dialog_vibrator_strength);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mValueText = (TextView) view.findViewById(R.id.current_value);

        mOldValue = Integer.parseInt(currentValue());
        mSeekBar = getSeekBar(view);
        mSeekBar.setMax(BACKLIGHT_MAX_BRIGHTNESS);
        mSeekBar.setProgress(mOldValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        updateProgress(mOldValue);
    }

    /**
     * @return	Read the value that currently in use, not the
     * 		one from sp. User might modify this value though
     * 		some kernel manager.
     *
     * */
    public static String currentValue() {
        return Utils.getFileValue(FILE_LED_LEFT, String.valueOf(BACKLIGHT_MAX_BRIGHTNESS));
    }

    private void updateProgress(int progress) {
        mValueText.setText(Integer.toString(Math.round(progress/ PROGRESS_OFFSET)) + "%");
    }

    private static void setValue(int newValue) {
	String newStrVal = newValue + "";
        Utils.writeValue(FILE_LED_LEFT, newStrVal);
        Utils.writeValue(FILE_LED_RIGHT, newStrVal);
    }

    public static void restore(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int storedValue = pref.getInt(DeviceSettings.KEY_BTN_BRIGHTNESS, BACKLIGHT_MAX_BRIGHTNESS);
        setValue(storedValue);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        setValue(progress);
	updateProgress(progress);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            final int value = mSeekBar.getProgress();
            setValue(value);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putInt(DeviceSettings.KEY_BTN_BRIGHTNESS, value);
            editor.apply();
        } else {
            restoreOldState();
        }
    }

    public static boolean isSupported() {
        return Utils.fileWritable(FILE_LED_LEFT) && Utils.fileWritable(FILE_LED_RIGHT);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
    }

    private void restoreOldState() {
        setValue(mOldValue);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
