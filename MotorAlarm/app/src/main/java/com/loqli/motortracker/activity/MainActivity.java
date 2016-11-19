package com.loqli.motortracker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loqli.motortracker.R;
import com.loqli.motortracker.api.ActiveResult;
import com.loqli.motortracker.api.ApiAdapter;
import com.loqli.motortracker.api.RegisterCallback;
import com.loqli.motortracker.api.impl.ApiAdapterImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener, LocationListener {

    public static final String DEVICE_REGISTERED = "device_registered";
    public static final String DEVICE_ON = "device_on";
    private static final String TRACKING_ACTIVE = "tracking";

    private static MainActivity instance;

    private float sensorMinimumShake = 0.1f;
    private volatile long lastSMS = 0l;
    private volatile long lastPoint = 0l;

    private final static String LAST_POINT = "lastPoint";
    private final static String LAST_SMS = "lastSMS";

    private double latitude = 0;
    private double longitude = 0;
    private String id = "007";

    private LocationManager locationManager;
    private long minTime = 30000;
    private float minDistance = 500;
    private Handler handler;
    private SharedPreferences prefs;

    private SensorManager sensorManager;
    private boolean gpsListeningOn = false;

    private ApiAdapter apiAdapter;
    long gpsTrackTimeInterval = 60000l;

    private long switchOffGpsTime = 3600000l;

    private Runnable switchOffGpsTask = new Runnable() {

        @Override
        public void run() {
            locationManager.removeUpdates(MainActivity.this);
        }

    };

    private volatile boolean tracking = false;
    protected volatile long lastChecked = 0l;
    private long sendNewSmsTime = 300000l;

    private String phoneNumber;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");

    private long lastLocationUpdate;
    private long lastSensorUpdate;
    private float accelerationThreshold = 1.2f;


    @SuppressLint("Wakelock")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.activity_main);

        TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        id = tMgr.getDeviceId();

        apiAdapter = new ApiAdapterImpl(this);

        if (savedInstanceState != null) {
            lastPoint = savedInstanceState.getLong(LAST_POINT);
            lastSMS = savedInstanceState.getLong(LAST_SMS);
        }

        handler = new Handler();

        prefs = getSharedPreferences("motoralarm", MODE_PRIVATE);
        phoneNumber = getSharedPreferences("motoralarm", MODE_PRIVATE).getString("other_phone", "");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "alarm wakelock");
        wakeLock.acquire();

        checkTracking();

        lastSensorUpdate = System.currentTimeMillis();

        if (!prefs.getBoolean(DEVICE_REGISTERED, false)) {

            apiAdapter.register(id, new RegisterCallback() {

                @Override
                public void isRegistered(boolean registered) {

                    Editor editor = prefs.edit();
                    editor.putBoolean(DEVICE_REGISTERED, registered);
                    editor.commit();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(LAST_SMS, lastSMS);
        outState.putLong(LAST_POINT, lastPoint);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // oh well
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (prefs.getBoolean(MainActivity.DEVICE_ON, true)) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager
                    .GRAVITY_EARTH);

            if (accelationSquareRoot >= accelerationThreshold) {

                if (event.timestamp - lastSensorUpdate < 200) {
                    return;
                }

                lastSensorUpdate = event.timestamp;
                Log.d("xtien", "motoralarm " + dateFormat.format(new Date()) + " " + accelationSquareRoot);

                long time = System.currentTimeMillis();

                if (time - lastSMS > sendNewSmsTime) {
                    lastSMS = time;

                    String random = Integer.toString((int) (Math.random() * 9999));

                    if (phoneNumber != null && phoneNumber.length() > 9) {
                        sendSMS(phoneNumber, random + " motor! " + dateFormat.format(new Date()));
                    }
                }

                if (!tracking) {

                    if (time - lastChecked > 1800000) {
                        lastChecked = time;
                        checkTracking();
                    }

                } else {

                    if (time - lastPoint > switchOffGpsTime) {

                        if (!gpsListeningOn) {
                            gpsListeningOn = true;
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                                    minDistance, this);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                                    minDistance, this);
                            handler.removeCallbacks(switchOffGpsTask);
                            handler.postDelayed(switchOffGpsTask, switchOffGpsTime);
                        }

                        lastPoint = time;
                        handler.removeCallbacks(sendPointTask);
                        handler.post(sendPointTask);
                    }
                }
            }
        }
    }

    Runnable sendPointTask = new Runnable() {

        @Override
        public void run() {

            if (latitude != 0 && longitude != 0) {
                apiAdapter.sendPoint(id, latitude, longitude);
            }

            handler.postDelayed(sendPointTask, gpsTrackTimeInterval);
        }

    };

    private void checkTracking() {
        apiAdapter.checkActive(id, latitude, longitude, new TrackingCallback() {

            @Override
            public void isActive(ActiveResult activeResult) {

                if (activeResult != null) {
                    try {
                        tracking = activeResult.isActive();
                        setPrefsTracking(tracking);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void setPrefsTracking(boolean tracking) {

        Editor editor = prefs.edit();
        editor.putBoolean(TRACKING_ACTIVE, tracking);
        editor.commit();
    }

    private void sendSMS(final String phoneNumber, final String message) {

        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        long now = System.currentTimeMillis();

        if (location.getProvider().equalsIgnoreCase(LocationManager.GPS_PROVIDER) || ((now - lastLocationUpdate) >
                120000)) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            lastLocationUpdate = now;

            Editor editor = prefs.edit();
            editor.putString("latitude", String.valueOf(latitude));
            editor.putString("longitude", String.valueOf(longitude));
            editor.commit();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onPause() {
        super.onPause();
        setPrefsTracking(tracking);
        unRegisterSensorListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        tracking = prefs.getBoolean(TRACKING_ACTIVE, true);

        if (prefs.getBoolean(DEVICE_ON, true)) {
            registerSensorListener();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void setDevice(boolean deviceOn) {

        if (sensorManager != null) {

            if (deviceOn) {
                registerSensorListener();
                lastSMS = 0l;
            } else {
                unRegisterSensorListener();
            }
        }
    }

    private void registerSensorListener() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unRegisterSensorListener() {
        sensorManager.unregisterListener(this);
    }

}
