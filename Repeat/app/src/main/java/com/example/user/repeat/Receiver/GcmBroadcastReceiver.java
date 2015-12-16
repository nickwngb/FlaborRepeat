package com.example.user.repeat.Receiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.user.repeat.Activity.Act_Login;
import com.example.user.repeat.R;
import com.example.user.repeat.UseForGCM.GoldBrotherGCM;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.i(getClass() + " GCM ERROR", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                Log.i(getClass() + " GCM DELETE", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                Log.i(getClass() + " GCM MESSAGE", extras.toString());
                Intent i = new Intent(context, Act_Login.class);
                i.setAction("android.intent.action.MAIN");
                i.addCategory("android.intent.category.LAUNCHER");
                GoldBrotherGCM.sendLocalNotification(context, NOTIFICATION_ID,
                        R.drawable.chrome, "你有一個通知", extras
                                .getString("message"), "gold brother", true,
                        PendingIntent.getActivity(context, 0, i,
                                PendingIntent.FLAG_CANCEL_CURRENT));
            }
        }
        setResultCode(Activity.RESULT_OK);
    }

}