package com.atriiapps.quranpakinurdu.Services;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atriiapps.quranpakinurdu.Activity.SuraActivity;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.ExternalConstants;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReciever extends BroadcastReceiver {
    Context mContext;
    Intent iRecever;
    Constants Constants = new Constants();

    Boolean isShowNotifications = true;

    @Override
    public void onReceive(Context context, Intent intent2) {
        mContext = context;
        iRecever = intent2;
        utils.log("rec", "running rec");
        pref_utils.PREF_INIT(context);


        isShowNotifications = pref_utils.get_Pref_Boolean(context, "show_notifications", true);

        if (isShowNotifications) {
            againSet();
            getData();

        }
    }

    private void againSet() {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(mContext, NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ExternalConstants.NotificationDelay, pendingIntent);

    }

    private void getData() {
        String version = pref_utils.get_Pref_String(mContext, "quran_version", Constants.DEFAULT_QURAN_TRANSLATION_VERSION_SHARED_PREF);

        String url = Constants.WEBSITE_BASE_URL + "?q=get_random&v=" + version;
        utils.log("noti", url + " ");


        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);

                String arabic = mainObj.getJSONObject("aya").getJSONArray("arabic").getJSONObject(0).getString("text");
                String translation = mainObj.getJSONObject("aya").getJSONArray("urdu").getJSONObject(0).getString("text");
                String sura = mainObj.getJSONObject("aya").getJSONArray("urdu").getJSONObject(0).getString("sura");
                String aya = mainObj.getJSONObject("aya").getJSONArray("urdu").getJSONObject(0).getString("aya");

                String ayaUrl = Constants.WEBSITE_BASE_URL + "?q=get_aya_from_sura&sura_no=" + sura + "&aya_no=" + aya + "&v=" + version;
                utils.log("noti", ayaUrl + " ");


                utils.log("noti", translation + " ");

                final String CHANNEL_ID = "Daily_Aya";


                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                Notification notification;

                Intent intent = new Intent(mContext, SuraActivity.class);
                intent.putExtra("sura_no", sura);
                intent.putExtra("aya_no", Integer.parseInt(aya));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                }
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notification = new Notification.Builder(mContext)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.last_read)
                            .setContentText(translation)
                            .setSubText("Boast your emaan")
                            .setContentIntent(pendingIntent)
                            .setChannelId(CHANNEL_ID)
                            .build();
                    notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_HIGH));

                } else {
                    notification = new Notification.Builder(mContext)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.last_read)
                            .setContentText(translation)
                            .setSubText("Boast your emaan")
                            .setContentIntent(pendingIntent)

                            .build();
                }
                notificationManager.notify(100, notification);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {
            utils.log("noti", error.toString());
            getData();

        });


        RequestQueue queue = Volley.newRequestQueue(mContext);

        queue.add(request);

    }
}
