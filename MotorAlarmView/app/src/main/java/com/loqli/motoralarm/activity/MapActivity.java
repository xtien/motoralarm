package com.loqli.motoralarm.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;
import com.loqli.motoralarm.R;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.points.PointsManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import roboguice.activity.RoboActivity;

public class MapActivity extends RoboActivity {

    @Inject
    private PointsManager pointsManager;

    private GoogleMap map;
    private Handler handler;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm");
    private long interval;
    private final float maxZoom = 16f;

    private List<Point> points = new ArrayList<Point>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        handler = new Handler();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        interval = Long.valueOf(getResources().getInteger(R.integer.interval)) * 1000l;

        points.add(new Point("dummy", 52.354701, 4.840103, System.currentTimeMillis()));

        getPoints();
    }

    private void getPoints() {

        new AsyncTask<Void, Void, List<Point>>() {

            @Override
            protected List<Point> doInBackground(Void... params) {
                try {
                    return pointsManager.getPoints();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(List<Point> result) {

                if(result !=null){
                    points = result;
                }

                if (points == null) {
                    points = new ArrayList<Point>();
                }

                if (points != null) {

                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                    for (Point point : points) {

                        double latitude = point.getLatitude();
                        double longitude = point.getLongitude();

                        String dateTime = dateFormat.format(new Date(point.getTime()));

                        MarkerOptions options = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title(dateTime);
                        Marker marker = map.addMarker(options);

                        boundsBuilder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = boundsBuilder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    map.moveCamera(cu);

                    float zoomLevel = map.getCameraPosition().zoom;
                    if (zoomLevel > maxZoom) {
                        map.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                    }
                }

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getPoints();
                    }

                }, interval);

            }

        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_activate:

                activate();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void activate() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                pointsManager.activate(true);
                return null;
            }

        }.execute();
    }
}
