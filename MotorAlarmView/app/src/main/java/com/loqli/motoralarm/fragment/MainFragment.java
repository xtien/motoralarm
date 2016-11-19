package com.loqli.motoralarm.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.loqli.motoralarm.R;
import com.loqli.motoralarm.activity.MapActivity;
import com.loqli.motoralarm.model.ActiveResult;
import com.loqli.motoralarm.points.PointsManager;
import com.loqli.motoralarm.prefs.MyPreferences;

import roboguice.fragment.RoboFragment;

/**
 * Created by christine on 19-11-14.
 */
public class MainFragment extends RoboFragment {

    @Inject
    private MyPreferences prefs;

    @Inject
    private PointsManager pointsManager;

    private Switch deviceOnSwitch;
    private Switch deviceActiveSwitch;
    private ImageView mapButton;

    private Button deviceIdEditButton;
    private Button devicePhoneEditButton;

    private volatile boolean isDeviceIdEdit = false;
    public boolean isDevicePhoneEdit = false;

    private TextView deviceIdView;
    private EditText deviceIdEdit;
    private TextView devicePhoneView;
    private EditText devicePhoneEdit;
    private Switch sirenOnSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, null);

        deviceIdView = (TextView) view.findViewById(R.id.deviceid);
        deviceIdEdit = (EditText) view.findViewById(R.id.deviceid_edit);

        devicePhoneView = (TextView) view.findViewById(R.id.device_phonenumber);
        devicePhoneEdit = (EditText) view.findViewById(R.id.device_phonenumber_edit);

        deviceOnSwitch = (Switch) view.findViewById(R.id.deviceonswitch);
        deviceActiveSwitch = (Switch) view.findViewById(R.id.deviceactiveswitch);

        mapButton = (ImageView) view.findViewById(R.id.map_button);

        deviceIdEditButton = (Button) view.findViewById(R.id.deviceid_edit_button);
        devicePhoneEditButton = (Button) view.findViewById(R.id.devicephone_edit_button);

        sirenOnSwitch = (Switch) view.findViewById(R.id.siren_on);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new AsyncTask<Void, Void, ActiveResult>() {

            @Override
            protected ActiveResult doInBackground(Void... params) {

                return pointsManager.isDeviceActive();
            }

            protected void onPostExecute(ActiveResult result) {

                if (result != null) {
                    deviceActiveSwitch.setChecked(result.isActive());
                }
            }

        }.execute();

        String deviceId = prefs.getDeviceId();
        String devicePhone = prefs.getDevicePhone();

        deviceIdView.setText(deviceId);
        deviceIdEdit.setText(deviceId);

        devicePhoneView.setText(devicePhone);
        devicePhoneEdit.setText(devicePhone);

        deviceActiveSwitch.setChecked(prefs.isDeviceActive());

        deviceOnSwitch.setChecked(prefs.isDeviceOn());
        sirenOnSwitch.setChecked(prefs.isSirenOn());

        deviceActiveSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = ((Switch) v).isChecked();
                prefs.setDeviceActive(checked);
                setDeviceActive(checked);
            }
        });

        deviceOnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = ((Switch) v).isChecked();
                prefs.setDeviceOn(checked);
                setDeviceOn(checked);
            }

        });

        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });


        deviceIdEditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isDeviceIdEdit) {

                    prefs.setDeviceId(deviceIdEdit.getText().toString());

                    deviceIdView.setText(deviceIdEdit.getText().toString());
                    deviceIdView.setVisibility(View.VISIBLE);
                    deviceIdEdit.setVisibility(View.GONE);
                    deviceIdEditButton.setText(getResources().getString(R.string.edit));

                } else {

                    deviceIdView.setVisibility(View.GONE);
                    deviceIdEdit.setVisibility(View.VISIBLE);
                    deviceIdEditButton.setText(getResources().getString(R.string.done));

                }

                isDeviceIdEdit = !isDeviceIdEdit;
            }
        });

        devicePhoneEditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isDevicePhoneEdit) {

                    prefs.setDevicePhone(devicePhoneEdit.getText().toString());

                    devicePhoneView.setText(devicePhoneEdit.getText().toString());
                    devicePhoneView.setVisibility(View.VISIBLE);
                    devicePhoneEdit.setVisibility(View.GONE);
                    devicePhoneEditButton.setText(getResources().getString(R.string.edit));

                } else {

                    devicePhoneView.setVisibility(View.GONE);
                    devicePhoneEdit.setVisibility(View.VISIBLE);
                    devicePhoneEditButton.setText(getResources().getString(R.string.done));

                }

                isDevicePhoneEdit = !isDevicePhoneEdit;
            }
        });

        sirenOnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prefs.setSirenOnOff(((Switch) v).isChecked());
            }
        });

    }

    private void setDeviceActive(final boolean checked) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                pointsManager.activate(checked);

            }
        }).start();
    }

    private void setDeviceOn(final boolean checked) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                String phoneNumber = prefs.getDevicePhone();

                if (phoneNumber != null && phoneNumber.length() > 8) {

                    String message = prefs.getDeviceId() + ":" + (checked ? "on" : "off");

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phoneNumber, null, message, null, null);

                } else {
                    Toast.makeText(getActivity(), "not a valid phone number", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }
}
