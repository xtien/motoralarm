package com.loqli.motoralarm.prefs;

/**
 * Created by christine on 19-11-14.
 */
public interface MyPreferences {

    static final String DEVICE_ON = "device_on";
    static final String DEVICE_ACTIVE = "device_active";
    static final String DEVICE_ID = "device_id";
    static final String DEVICE_PHONE_NUMBER = "device_phone";
    static final String ALARM_ON = "alarm_on";

    boolean isDeviceActive();

    boolean isDeviceOn();

    void setDeviceActive(boolean checked);

    void setDeviceOn(boolean checked);

    String getDeviceId();

    void setDeviceId(String id);

    String getDevicePhone();

    void setDevicePhone(String phone);

    boolean isSirenOn();

    void setSirenOnOff(boolean on);
}
