package com.loqli.motoralarm.points.impl;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.internal.ma;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loqli.motoralarm.R;
import com.loqli.motoralarm.model.ActiveResult;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.model.PointsResult;
import com.loqli.motoralarm.points.DeviceActiveCallback;
import com.loqli.motoralarm.points.PointsManager;
import com.loqli.motoralarm.prefs.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import roboguice.inject.InjectResource;

@Singleton
public class PointsManagerImpl implements PointsManager {

    private static final String TAG = PointsManagerImpl.class.getSimpleName();

    private final MyPreferences prefs;
    private ObjectMapper mapper;

    @InjectResource(R.string.base_url_string)
    private String baseUrlString;

    @InjectResource(R.integer.interval)
    private int intervalInt;

    private String id;

    private long interval;

    private int readTimeout = 10000;
    private int connectTimeout = 15000;

    @Inject
    public PointsManagerImpl(ObjectMapper mapper, MyPreferences prefs) {

        this.mapper = mapper;
        this.prefs = prefs;

        interval = Long.valueOf(intervalInt) * 1000;

    }

    @Override
    public List<Point> getPoints() throws ClientProtocolException, IOException {

        List<Point> points = new ArrayList<Point>();
        long i = 1l;

        while (points != null && (points.size() == 0 || i > 100)) {
            points = getPoints(interval * i);
            i = (i == 1l) ? 2 : i * i;
        }

        return points;
    }

    private List<Point> getPoints(long interval) throws ClientProtocolException, IOException {

        List<Point> points = null;

        String urlString = baseUrlString + "getPoints/" + id + "/interval/" + interval;

        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {

                InputStream is = connection.getInputStream();
                PointsResult pointsResult = mapper.readValue(is, PointsResult.class);
                if (pointsResult != null) {
                    points = pointsResult.getPoints();
                }

            } else {
                Log.e(TAG, connection.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    @Override
    public int activate(boolean b) {

        int httpResult = -1;

        try {

            String urlString = baseUrlString + "activate/";
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
            point.put("activate", b);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(point.toString());
            wr.flush();
            wr.close();

            httpResult = connection.getResponseCode();
            if (httpResult != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, connection.getResponseMessage());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpResult;
    }

    @Override
    public ActiveResult isDeviceActive() {

        String urlString = baseUrlString + "checkActive/" + id;

        try {

            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {

                InputStream is = connection.getInputStream();
                ActiveResult result = mapper.readValue(is, ActiveResult.class);
                if (result != null) {
                    prefs.setDeviceActive(result.isActive());
                    return result;
                }

            } else {
                Log.e(TAG, connection.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
