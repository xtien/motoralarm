package com.loqli.motoralarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.loqli.motoralarm.R;
import com.loqli.motoralarm.prefs.MyPreferences;

import java.util.LinkedList;
import java.util.List;

import roboguice.RoboGuice;

/**
 * Created by christine on 22-11-14.
 */
public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    private Context context;
    private Handler handler;
    private int count = 4;
    private MyPreferences prefs;
    private static List<String> previousMessageKeys = new LinkedList<String>();

    public void onReceive(Context context, Intent intent) {

        this.context = context;
        handler = new Handler();

        final Bundle bundle = intent.getExtras();

        prefs = RoboGuice.getInjector(context).getInstance(MyPreferences.class);

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String string = prefs.getDevicePhone();

                    if (phoneNumber.equals(prefs.getDevicePhone())) {

                        String key = currentMessage.getMessageBody().substring(0, 4);

                        if (!previousMessageKeys.contains(key)) {
                            playSound();
                            previousMessageKeys.add(key);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private void playSound() {

        if (prefs.isSirenOn()) {

            // MediaPlayer.create(context, R.raw.police).start();

            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/"
                    + R.raw.police);
            Ringtone r = RingtoneManager.getRingtone(context, soundUri);
            r.play();

            if (count-- > 0) {

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        playSound();

                    }
                }, 20000);
            }
        }
    }
}
