package com.loqli.motortracker.api.impl;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loqli.motortracker.activity.TrackingCallback;
import com.loqli.motortracker.api.ActiveResult;
import com.loqli.motortracker.api.ApiAdapter;
import com.loqli.motortracker.api.RegisterCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by christine on 18-11-14.
 */
public class ApiAdapterImpl implements ApiAdapter {

    private static final String TAG = ApiAdapterImpl.class.getSimpleName();

    private final Context context;
    private final ExecutorService pool;

    private static String server = "munin.christine.nl";
    private static int port = 9010;

    private volatile boolean tracking = false;
    protected volatile long lastChecked = 0;

    private ObjectMapper mapper;

    private static int readTimeout = 10000;
    private static  int connectTimeout = 15000;
    private volatile double previousLatitude = 0d;
    private  volatile double previousLongitude = 0d;

    public ApiAdapterImpl(Context context) {

        this.context = context;
        mapper = new ObjectMapper();

        pool = Executors.newCachedThreadPool();
    }

    public void checkActive(final String id, final double latitude, final double longitude, TrackingCallback callback) {
        pool.execute(new CheckActiveTask(id, latitude, longitude, callback));
    }

    @Override
    public void sendPoint(final String id, final double latitude, final double longitude) {

        if (latitude != previousLatitude && longitude != previousLongitude) {
           pool.execute(new SendPointTask(id, latitude, longitude));
        }
    }

    @Override
    public void register(final String id, final RegisterCallback callback) {
        pool.execute(new RegisterTask(id, callback));
    }

    class SendPointTask implements Runnable {

        private final String id;
        private double latitude;
        private double longitude;

        public SendPointTask(String id, double latitude, double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
        }


        @Override
        public void run() {

            String time = String.valueOf(System.currentTimeMillis());

            String urlString = "http://" + server + ":" + port + "/motoralarm/addPoint/";

            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(readTimeout);
                connection.setConnectTimeout(connectTimeout);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                JSONObject point = new JSONObject();

                point.put("id", id);
                point.put("latitude", latitude);
                point.put("longitude", longitude);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(point.toString());
                wr.flush();
                wr.close();

                int HttpResult = connection.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, connection.getResponseMessage());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            previousLatitude = latitude;
            previousLongitude = longitude;
        }
    }

    class RegisterTask implements Runnable {

        private final String id;
        private final RegisterCallback callback;

        public RegisterTask(String id, final RegisterCallback callback) {
            this.id = id;
            this.callback = callback;
        }

        @Override
        public void run() {

            String time = String.valueOf(System.currentTimeMillis());

            String urlString = "http://" + server + ":" + port + "/motoralarm/registerDevice/";

            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(readTimeout);
                connection.setConnectTimeout(connectTimeout);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                JSONObject point = new JSONObject();

                point.put("id", id);
                point.put("active", true);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(point.toString());
                wr.flush();
                wr.close();

                int HttpResult = connection.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, connection.getResponseMessage());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class CheckActiveTask implements Runnable {

        private final String id;
        private final TrackingCallback callback;
        private double latitude;
        private double longitude;

        public CheckActiveTask(final String id, final double latitude, final double longitude,
                               TrackingCallback callback) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.callback = callback;
        }


        @Override
        public void run() {

            String urlString = "http://" + server + ":" + port + "/motoralarm/checkActive/" + id;

            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {

                    InputStream is = connection.getInputStream();
                    ActiveResult activeResult = mapper.readValue(is, ActiveResult.class);
                    callback.isActive(activeResult);

                    if (activeResult != null && activeResult.isActive()) {
                        pool.execute(new SendPointTask(id, latitude, longitude));
                    }

                } else {
                    Log.e(TAG, connection.getResponseMessage());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
