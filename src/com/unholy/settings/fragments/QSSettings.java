/*
 * Copyright (C) 2017 Unholy Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unholy.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.unholy.settings.preference.CustomSeekBarPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.unholy.settings.preference.SystemSettingSwitchPreference;

public class QSSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String QS_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String QS_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String QS_COLUMNS_PORTRAIT = "qs_columns_portrait";
    private static final String QS_COLUMNS_LANDSCAPE = "qs_columns_landscape";

    private ListPreference mQsRowsPort;
    private ListPreference mQsRowsLand;
    private ListPreference mQsColumnsPort;
    private ListPreference mQsColumnsLand;
    private ListPreference mQuickPulldown; 
    ListPreference mSmartPulldown;
    private CustomSeekBarPreference mQsPanelAlpha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_settings);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 1, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);

        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        mQsPanelAlpha = (CustomSeekBarPreference) findPreference(QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_BG_ALPHA, 255, UserHandle.USER_CURRENT);
        mQsPanelAlpha.setValue(qsPanelAlpha);
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mQsRowsPort = (ListPreference) findPreference(QS_ROWS_PORTRAIT);
        mQsRowsPort.setOnPreferenceChangeListener(this);
        int rowsPort = Settings.System.getIntForUser(resolver,
                Settings.System.QS_ROWS_PORTRAIT, 3, UserHandle.USER_CURRENT);
        mQsRowsPort.setValue(String.valueOf(rowsPort));

        mQsRowsLand = (ListPreference) findPreference(QS_ROWS_LANDSCAPE);
        mQsRowsLand.setOnPreferenceChangeListener(this);
        int rowsLand = Settings.System.getIntForUser(resolver,
                Settings.System.QS_ROWS_LANDSCAPE, 2, UserHandle.USER_CURRENT);
        mQsRowsLand.setValue(String.valueOf(rowsLand));

        mQsColumnsPort = (ListPreference) findPreference(QS_COLUMNS_PORTRAIT);
        mQsColumnsPort.setOnPreferenceChangeListener(this);
        int colPort = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLUMNS_PORTRAIT, 4, UserHandle.USER_CURRENT);
        mQsColumnsPort.setValue(String.valueOf(colPort));

        mQsColumnsLand = (ListPreference) findPreference(QS_COLUMNS_LANDSCAPE);
        mQsColumnsLand.setOnPreferenceChangeListener(this);
        int colLand = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLUMNS_LANDSCAPE, 5, UserHandle.USER_CURRENT);
        mQsColumnsLand.setValue(String.valueOf(colLand));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN,
                    quickPulldownValue, UserHandle.USER_CURRENT);
            updatePulldownSummary(quickPulldownValue);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_PANEL_BG_ALPHA, bgAlpha,
                    UserHandle.USER_CURRENT);
        } else if (preference == mQsRowsPort) {
            int QsRowsPort = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_ROWS_PORTRAIT, QsRowsPort);
            return true;
        } else if (preference == mQsRowsLand) {
            int QsRowsLand = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_ROWS_LANDSCAPE, QsRowsLand);
            return true;
        } else if (preference == mQsColumnsPort) {
            int QsColumnsPort = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_COLUMNS_PORTRAIT, QsColumnsPort);
            return true;
        } else if (preference == mQsColumnsLand) {
            int QsColumnsLand = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_COLUMNS_LANDSCAPE, QsColumnsLand);
            return true;
        }
        return false;
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else if (value == 3) {
            // quick pulldown always
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary_always));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_left
                    : R.string.quick_pulldown_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }     	    
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.UNHOLY_SETTINGS;
    }
}