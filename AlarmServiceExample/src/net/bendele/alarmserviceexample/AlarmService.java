package net.bendele.alarmserviceexample;

import com.jakewharton.notificationcompat2.NotificationCompat2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

    NotificationCompat2.Builder notification;
    PendingIntent pendingIntent;

 // Logging constants
    private static final boolean DEBUG = true;
    private static final String BENDELE = "BENDELE";
    private static final String CLASS = "AlarmService - ";

    private static void myLog(String msg) {
        if (DEBUG) {
            if (msg != "") {
                msg = " - " + msg;
            }
            String caller = Thread.currentThread().getStackTrace()[3]
                    .getMethodName();
            Log.d(BENDELE, CLASS + caller + msg);
        }
    }

    @Override
    public void onCreate() {
        myLog("");
        super.onCreate();

        notification = new NotificationCompat2.Builder(getApplicationContext());
        notification.setAutoCancel(false).setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Run / Walk Intervals")
                .setSmallIcon(R.drawable.ic_launcher).setOngoing(true);

        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, main,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        myLog("");
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        myLog("");
        Bundle extras = intent.getExtras();
        String state = extras.getString("State");

        notification.setContentText(state).setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());
        startForeground(2, notification.build());

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        myLog("");
        return null;
    }

}
