package com.loqli.motortracker.activity;

import com.loqli.motortracker.api.ActiveResult;

/**
 * Created by christine on 24-11-14.
 */
public interface TrackingCallback {

    public void isActive(ActiveResult activeResult);
}
