package net.bendele.alarmserviceexample;

/***
 Copyright (c) 2008-2011 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Advanced Android Development_
 http://commonsware.com/AdvAndroid
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class TestAlarmService extends WakefulIntentService {

    public static final String BROADCAST_ACTION = "net.bendele.runwalk.displayevent.testing";

    // Logging constants
    private static final boolean DEBUG = true;
    private static final String BENDELE = "BENDELE";
    private static final String CLASS = "TestAlarmService - ";

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

    public TestAlarmService() {
        super("TestAlarmService");
        myLog("");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        myLog("");
        Intent newIntent = new Intent(BROADCAST_ACTION);
        if (MainApp.isScopeGlobal()) {
            sendBroadcast(newIntent);
        } else {
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(newIntent);
        }
    }
}
