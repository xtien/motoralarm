package com.loqli.motortracker.api;

import com.loqli.motortracker.activity.TrackingCallback;

/**
 * Created by christine on 18-11-14.
 */
public interface ApiAdapter {

    void checkActive(String id, double latitude, double longitude, TrackingCallback callback);

    void sendPoint(String id, double latitude, double longitude);

    void register(String id, RegisterCallback registerCallback);
}
