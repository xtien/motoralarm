package com.loqli.motoralarm.activity;

import android.os.Bundle;

import com.google.inject.Inject;
import com.loqli.motoralarm.R;
import com.loqli.motoralarm.prefs.MyPreferences;

import roboguice.activity.RoboFragmentActivity;

/**
 * Created by christine on 19-11-14.
 */
public class MainActivity extends RoboFragmentActivity {

    @Inject
    private MyPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
     }

}
