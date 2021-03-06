package com.evervolv.toolbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.evervolv.toolbox.R;
import com.evervolv.toolbox.Toolbox;
import com.evervolv.toolbox.misc.FileUtil;

public class DeviceTouchscreen extends DeviceSettingFragment {

    private static final String KEY_SWEEP_TO_WAKE = "pref_sweep_to_wake";
    private static final String SWEEP_2_WAKE_FILE = "/sys/android_touch/sweep2wake";
    private CheckBoxPreference mSweep2Wake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        addPreferencesFromResource(R.xml.device_touchscreen);

        mSweep2Wake = (CheckBoxPreference) findPreference(KEY_SWEEP_TO_WAKE);
        mSweep2Wake.setEnabled(isSweep2WakeSupported() && Toolbox.isEnabled(getActivity()));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSweep2Wake) {
            toggleS2w(mSweep2Wake.isChecked());
        }
        return true;
    }

    private static void toggleS2w(boolean enable) {
        if (enable) {
            FileUtil.fileWriteOneLine(SWEEP_2_WAKE_FILE, "1\n");
        } else {
            FileUtil.fileWriteOneLine(SWEEP_2_WAKE_FILE, "0\n");
        }
    }

    private static boolean isSweep2WakeSupported() {
        return FileUtil.fileExists(SWEEP_2_WAKE_FILE);
    }

    @Override
    public void restore(Context context, boolean toolboxEnabled) {
        if (!isSweep2WakeSupported()) { return; }
        if (mSweep2Wake != null) {
            mSweep2Wake.setEnabled(toolboxEnabled);
        }
        if (toolboxEnabled) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean enabled = sharedPrefs.getBoolean(KEY_SWEEP_TO_WAKE, false);
            toggleS2w(enabled);
        } else {
            toggleS2w(false);
        }
    }

}
