package org.omnirom.device;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.omnirom.device.Preference.SpectrumSwitchPreference;

import static org.omnirom.device.Preference.SpectrumPreference.FEATURE;
import static org.omnirom.device.Preference.SpectrumPreference.SPECTRUM_PREFERENCE_ADD_QS_TILE;

/**
 * TileService for spectrum profile switch
 *
 * Created by 0ranko0P <ranko0p@outlook.com> on 2020.01.10
 * */
public final class SpectrumTileService extends TileService {

    private Tile mTile;
    private SharedPreferences sp;

    private String[] level;
    private String[] profiles;

    /**
     * Update the profile only when the user closes the QS settings.
     * This will ensure the profile update only happen once and
     * avoid unnecessary I/O operation.
     *
     * @see SpectrumTileService#onStopListening()
     * */
    private int finalIndex = -1;
    private int oldIndex;

    @Override
    public IBinder onBind(Intent intent) {
        level = getResources().getStringArray(R.array.spectrum_values);
        profiles = getResources().getStringArray(R.array.spectrum_profiles);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        // disable prompt dialog on preference
        if (sp.getBoolean(SPECTRUM_PREFERENCE_ADD_QS_TILE, true))
            sp.edit().putBoolean(SPECTRUM_PREFERENCE_ADD_QS_TILE, false).apply();
        return super.onBind(intent);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mTile = getQsTile();
        mTile.setIcon(Icon.createWithResource(this, R.drawable.ic_spectrum));

        if (!SpectrumSwitchPreference.isEnabled(sp)) {
            mTile.setState(Tile.STATE_UNAVAILABLE);
            mTile.updateTile();
            return;
        } else {
            mTile.setState(Tile.STATE_ACTIVE);
        }

        oldIndex = remapPowerLevel(FEATURE.getCurrentValue());

        mTile.setLabel(profiles[oldIndex]);
        mTile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        mTile.setLabel(profiles[switchProfile()]);
        mTile.updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (finalIndex != -1 && finalIndex != oldIndex) {
            getResources().getStringArray(R.array.spectrum_values);
            String newProfile = level[finalIndex];
            if (FEATURE.applyValue(newProfile))
                FEATURE.applySharedPreferences(newProfile, PreferenceManager.getDefaultSharedPreferences(this));
        }
    }

    private int switchProfile() {
        // make it loop
        final int current = (finalIndex == -1) ? oldIndex : finalIndex;
        finalIndex = (current == profiles.length - 1) ? 0 : current + 1;
        return finalIndex;
    }

    private int remapPowerLevel(String current) {
        int index = 0;
        while (index < level.length) {
            if (level[index].equals(current)) return index;
            index++;
        }
        return 0;
    }
}
