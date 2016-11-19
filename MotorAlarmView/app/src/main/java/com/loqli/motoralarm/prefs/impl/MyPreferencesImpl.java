package com.loqli.motoralarm.prefs.impl;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loqli.motoralarm.prefs.MyPreferences;

/**
 * Created by christine on 19-11-14.
 */
@Singleton
public class MyPreferencesImpl implements MyPreferences {

    @Inject
    private SharedPreferences prefs;

    @Override
    public boolean isDeviceActive() {
        return prefs.getBoolean(DEVICE_ACTIVE, false);
    }

    @Override
    public void setDeviceActive(boolean checked) {
        set(DEVICE_ACTIVE, checked);
    }

    @Override
    public boolean isDeviceOn() {
        return prefs.getBoolean(DEVICE_ON, false);
    }

     @Override
    public void setDeviceOn(boolean checked) {
        set(DEVICE_ON, checked);
    }

    @Override
    public String getDeviceId() {
        return prefs.getString(DEVICE_ID, null);
    }

    @Override
    public void setDeviceId(String id) {
        set(DEVICE_ID, id);
    }

    @Override
    public String getDevicePhone() {
        return prefs.getString(DEVICE_PHONE_NUMBER, null);
    }

    @Override
    public void setDevicePhone(String phone) {
        set(DEVICE_PHONE_NUMBER, phone);
    }

    @Override
    public boolean isSirenOn() {
        return prefs.getBoolean(ALARM_ON, true);
    }

    @Override
    public void setSirenOnOff(boolean on) {
        set(ALARM_ON, on);
    }

    private void set(String key, Object value) {

        SharedPreferences.Editor editor = prefs.edit();

        if (value == null) {
            editor.putString(key, null);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }

        editor.commit();
    }
}
