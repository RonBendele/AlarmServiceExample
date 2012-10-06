package net.bendele.alarmserviceexample;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    private AlarmManager runAlarmMgr;
    private Intent intent;
    private PendingIntent runPendingIntent;
    private Context context;

    // Logging constants
    private static final boolean DEBUG = true;
    private static final String BENDELE = "BENDELE";
    private static final String CLASS = "MainActivity - ";

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
    public void onCreate(Bundle savedInstanceState) {
        myLog("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        runAlarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, OnRunAlarmReceiver.class);
        // runPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // runPendingIntent = PendingIntent.getService(context, 0, intent, 0);
        runPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLog("");
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(runBroadcastReceiver);
        // unregisterReceiver(runBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLog("");
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(runBroadcastReceiver,
                        new IntentFilter(RunAlarmService.BROADCAST_ACTION));
        // registerReceiver(runBroadcastReceiver, new IntentFilter(
        // RunAlarmService.BROADCAST_ACTION));
    }

    private void showDialog() {
        AlertDialog.Builder ttsAlertBuilder = new AlertDialog.Builder(this);
        ttsAlertBuilder.setCancelable(true).setTitle("onReceive")
                .setMessage("runBroadcastReceiver.onReceive() was called")
                .setPositiveButton(R.string.ok, null).show();
    }

    private BroadcastReceiver runBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myLog("");
            showDialog();
        }
    };

    public void onButtonClick(View view) {
        myLog("");
        long now = SystemClock.elapsedRealtime();
        /*
         * runAlarmMgr.set(AlarmManager.ELAPSED_REALTIME, now + 0,
         * runPendingIntent);
         * runAlarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, now + 0,
         * runPendingIntent); runAlarmMgr.set(AlarmManager.RTC, now + 0,
         * runPendingIntent);
         */
        runAlarmMgr.set(AlarmManager.RTC_WAKEUP, now + 0, runPendingIntent);

        // runAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, now
        // + runTriggerTime, runIntervalTime, runPendingIntent);
    }
}
