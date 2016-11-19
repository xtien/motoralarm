package com.loqli.motortracker.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.loqli.motortracker.R;
import com.loqli.motortracker.activity.MainActivity;

/**
 * Created by christine on 19-11-14.
 */
public class MainFragment extends Fragment {

    private TextView deviceIdView;
    private TextView otherPhoneNumberView;
    private EditText otherPhoneNumberEdit;
    private Button editButton;
    private volatile boolean isEdit = false;
    private ImageView onOffView;
    private String deviceId;
    private Context context;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getActivity().getApplicationContext();

        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        deviceId = tMgr.getDeviceId();

        prefs = context.getSharedPreferences("motoralarm", getActivity().MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("device_id", deviceId);
        editor.commit();

        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, null);

        deviceIdView = (TextView) view.findViewById(R.id.deviceid);
        otherPhoneNumberView = (TextView) view.findViewById(R.id.otherphone_number);
        otherPhoneNumberEdit = (EditText) view.findViewById(R.id.otherphone_number_edit);
        editButton = (Button) view.findViewById(R.id.edit_button);
        onOffView = (ImageView) view.findViewById(R.id.on_off);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceIdView.setText(deviceId);
        otherPhoneNumberView.setText(prefs.getString("other_phone", ""));
        otherPhoneNumberEdit.setText(prefs.getString("other_phone", ""));

        onOffView.setImageResource(prefs.getBoolean(MainActivity.DEVICE_ON, false) ? R.drawable.green_dot : R
                .drawable.red_dot);

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isEdit) {

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("other_phone", otherPhoneNumberEdit.getText().toString());
                    edit.commit();
                    otherPhoneNumberView.setText(otherPhoneNumberEdit.getText().toString());
                    otherPhoneNumberView.setVisibility(View.VISIBLE);
                    otherPhoneNumberEdit.setVisibility(View.GONE);
                    editButton.setText(getResources().getString(R.string.edit));

                } else {

                    otherPhoneNumberView.setVisibility(View.GONE);
                    otherPhoneNumberEdit.setVisibility(View.VISIBLE);
                    editButton.setText(getResources().getString(R.string.done));

                }

                isEdit = !isEdit;
            }
        });
    }

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            boolean b = sharedPreferences.getBoolean(MainActivity.DEVICE_ON, false);
            onOffView.setImageResource(b ? R.drawable.green_dot : R.drawable.red_dot);
        }
    };
}
