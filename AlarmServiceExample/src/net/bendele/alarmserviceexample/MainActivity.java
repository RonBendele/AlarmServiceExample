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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private AlarmManager runAlarmMgr;
    private Intent intent;
    private PendingIntent runPendingIntent;
    private Context context;

    private Button startER;
    private Button startERW;
    private Button startRTC;
    private Button startRTCW;
    private Button stopAlarm;
    private RadioGroup pendingIntentType;
    private RadioGroup broadcastScope;
    private Spinner minutes;
    private int interval;
    private static boolean isERAvailable = false;

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
        myLog("in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        if (!isERAvailable) {
            setElapsedRealtimeAlarmAvailability();
        }

        runAlarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, OnRunAlarmReceiver.class);
        myLog("Local Broadcast");
        MainApp.setScopeGlobal(false);
        myLog("Pending intent will perform a Broadcast");
        runPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        startER = (Button) findViewById(R.id.startERButton);
        startERW = (Button) findViewById(R.id.startERWButton);
        startRTC = (Button) findViewById(R.id.startRTCButton);
        startRTCW = (Button) findViewById(R.id.startRTCWButton);
        stopAlarm = (Button) findViewById(R.id.stopAlarmButton);

        pendingIntentType = (RadioGroup) findViewById(R.id.rgPendingIntentType);
        pendingIntentType
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        myLog("");
                        switch (checkedId) {
                        case R.id.radioactivity:
                            myLog("Pending intent will start a new Activity");
                            runPendingIntent = PendingIntent.getActivity(
                                    context, 0, intent, 0);
                            break;
                        case R.id.radioservice:
                            myLog("Pending intent will start a Service");
                            runPendingIntent = PendingIntent.getService(
                                    context, 0, intent, 0);
                            break;
                        case R.id.radiobroadcast:
                            myLog("Pending intent will perform a Broadcast");
                            runPendingIntent = PendingIntent.getBroadcast(
                                    context, 0, intent, 0);
                            break;
                        default:
                            myLog("Default");
                        }
                    }
                });

        broadcastScope = (RadioGroup) findViewById(R.id.rgbroadcast);
        broadcastScope
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                        case R.id.rblocal:
                            myLog("Local Broadcast");
                            unregisterReceiver(runBroadcastReceiver);
                            LocalBroadcastManager
                                    .getInstance(context)
                                    .registerReceiver(
                                            runBroadcastReceiver,
                                            new IntentFilter(
                                                    RunAlarmService.BROADCAST_ACTION));
                            MainApp.setScopeGlobal(false);
                            break;
                        case R.id.rbglobal:
                            myLog("Global Broadcast");
                            LocalBroadcastManager.getInstance(context)
                                    .unregisterReceiver(runBroadcastReceiver);
                            registerReceiver(runBroadcastReceiver,
                                    new IntentFilter(
                                            RunAlarmService.BROADCAST_ACTION));
                            MainApp.setScopeGlobal(true);
                            break;
                        default:
                            myLog("Default");
                        }
                    }
                });

        minutes = (Spinner) findViewById(R.id.minutesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.minutes, android.R.layout.simple_spinner_item);
        minutes.setAdapter(adapter);
        minutes.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adView, View target,
                    int position, long id) {
                Spinner spinner = (Spinner) adView;
                interval = Integer.parseInt(spinner.getItemAtPosition(position)
                        .toString());
                myLog("User chose an interval of " + interval);
                interval = interval * 1000;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adView) {
                // leave everything as is
            }
        });
        minutes.setSelection(9); // start with 10 seconds
        myLog("out");
    } // onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLog("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLog("");
        runAlarmMgr.cancel(runPendingIntent);
        if (MainApp.isScopeGlobal()) {
            unregisterReceiver(runBroadcastReceiver);
            unregisterReceiver(testBroadcastReceiver);
        } else {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(
                    runBroadcastReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(
                    testBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLog("in");

        startER.setEnabled(isERAvailable);
        startERW.setEnabled(isERAvailable);
        startRTC.setEnabled(true);
        startRTCW.setEnabled(true);
        stopAlarm.setEnabled(false);

        pendingIntentType.setVisibility(View.VISIBLE);
        broadcastScope.setVisibility(View.VISIBLE);
        minutes.setEnabled(true);

        if (MainApp.isScopeGlobal()) {
            registerReceiver(runBroadcastReceiver, new IntentFilter(
                    RunAlarmService.BROADCAST_ACTION));
            registerReceiver(testBroadcastReceiver, new IntentFilter(
                    TestAlarmService.BROADCAST_ACTION));
        } else {
            LocalBroadcastManager.getInstance(context).registerReceiver(
                    runBroadcastReceiver,
                    new IntentFilter(RunAlarmService.BROADCAST_ACTION));
            LocalBroadcastManager.getInstance(context).registerReceiver(
                    testBroadcastReceiver,
                    new IntentFilter(TestAlarmService.BROADCAST_ACTION));
        }
        myLog("out");
    }

    private void setElapsedRealtimeAlarmAvailability() {
        myLog("");
        AlarmManager testAlarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent testIntent = new Intent(context, OnTestAlarmReceiver.class);
        PendingIntent testPendingIntent = PendingIntent.getBroadcast(context,
                0, testIntent, 0);
        long now = SystemClock.elapsedRealtime();
        testAlarmMgr.set(AlarmManager.ELAPSED_REALTIME, now, testPendingIntent);
    }

    private BroadcastReceiver testBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myLog("isERAvailable is true");
            isERAvailable = true;
            startER.setEnabled(isERAvailable);
            startERW.setEnabled(isERAvailable);
        }
    };

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

    public void onStopAlarmButtonClick(View view) {
        myLog("");
        runAlarmMgr.cancel(runPendingIntent);
        startER.setEnabled(isERAvailable);
        startERW.setEnabled(isERAvailable);
        startRTC.setEnabled(true);
        startRTCW.setEnabled(true);
        stopAlarm.setEnabled(false);
        pendingIntentType.setVisibility(View.VISIBLE);
        broadcastScope.setVisibility(View.VISIBLE);
        minutes.setEnabled(true);
    }

    public void onStartERButtonClick(View view) {
        myLog("");
        // because Elapsed Real-time, use SystemClock
        long now = SystemClock.elapsedRealtime();
        runAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, now + 0,
                interval, runPendingIntent);
        startER.setEnabled(false);
        startERW.setEnabled(false);
        startRTC.setEnabled(false);
        startRTCW.setEnabled(false);
        stopAlarm.setEnabled(true);
        pendingIntentType.setVisibility(View.INVISIBLE);
        broadcastScope.setVisibility(View.INVISIBLE);
        minutes.setEnabled(false);
    }

    public void onStartERWButtonClick(View view) {
        myLog("");
        // because Elapsed Real-time, use SystemClock
        long now = SystemClock.elapsedRealtime();
        runAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, now + 0,
                interval, runPendingIntent);
        startER.setEnabled(false);
        startERW.setEnabled(false);
        startRTC.setEnabled(false);
        startRTCW.setEnabled(false);
        stopAlarm.setEnabled(true);
        pendingIntentType.setVisibility(View.INVISIBLE);
        broadcastScope.setVisibility(View.INVISIBLE);
        minutes.setEnabled(false);
    }

    public void onStartRTCButtonClick(View view) {
        myLog("");
        // because Real-Time Clock, use System
        long now = System.currentTimeMillis();
        runAlarmMgr.setRepeating(AlarmManager.RTC, now + 0, interval,
                runPendingIntent);
        startER.setEnabled(false);
        startERW.setEnabled(false);
        startRTC.setEnabled(false);
        startRTCW.setEnabled(false);
        stopAlarm.setEnabled(true);
        pendingIntentType.setVisibility(View.INVISIBLE);
        broadcastScope.setVisibility(View.INVISIBLE);
        minutes.setEnabled(false);
    }

    public void onStartRTCWButtonClick(View view) {
        myLog("");
        // because Real-Time Clock, use System
        long now = System.currentTimeMillis();
        runAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, now + 0, interval,
                runPendingIntent);
        startER.setEnabled(false);
        startERW.setEnabled(false);
        startRTC.setEnabled(false);
        startRTCW.setEnabled(false);
        stopAlarm.setEnabled(true);
        pendingIntentType.setVisibility(View.INVISIBLE);
        broadcastScope.setVisibility(View.INVISIBLE);
        minutes.setEnabled(false);
    }

}
