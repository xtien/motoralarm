package com.loqli.motortracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.loqli.motortracker.activity.MainActivity;

/**
 * Created by christine on 20-11-14.
 */
public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                SharedPreferences prefs = context.getSharedPreferences("motoralarm", Context.MODE_PRIVATE);

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    String deviceId = prefs.getString("device_id", null);

                    if (message.contains(":")) {
                        String[] stringArray = message.split(":");
                        if (stringArray[0].equalsIgnoreCase(prefs.getString("device_id", null))) {

                            Boolean deviceOn = null;

                            if (stringArray[1].equals("on")) {
                                deviceOn = true;
                            } else if (stringArray[1].equalsIgnoreCase("off")) {
                                deviceOn = false;
                            }

                            if (deviceOn != null) {
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putBoolean(MainActivity.DEVICE_ON, deviceOn);
                                edit.commit();
                            }

                            MainActivity.getInstance().setDevice(deviceOn);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
