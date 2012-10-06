/***
	Copyright (c) 2009 CommonsWare, LLC

	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package net.bendele.alarmserviceexample;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

abstract public class WakefulIntentService extends IntentService {

    // Logging constants
    private static final boolean DEBUG = true;
    private static final String BENDELE = "BENDELE";
    private static final String CLASS = "WakefulIntentService - ";

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

    abstract protected void doWakefulWork(Intent intent);

    static final String NAME = "net.bendele.runwalk.WakefulIntentService";
    private static volatile PowerManager.WakeLock lockStatic = null;

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        myLog("");
        if (lockStatic == null) {
            myLog("LockStatic == null");
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);

            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    public static void sendWakefulWork(Context ctxt, Intent i) {
        myLog("");
        getLock(ctxt.getApplicationContext()).acquire();
        ctxt.startService(i);
    }

    public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
        myLog("");
        sendWakefulWork(ctxt, new Intent(ctxt, clsService));
    }

    public WakefulIntentService(String name) {
        super(name);
        myLog("");
        setIntentRedelivery(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myLog("");
        PowerManager.WakeLock lock = getLock(this.getApplicationContext());
        if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
            lock.acquire();
        }
        super.onStartCommand(intent, flags, startId);
        return (START_REDELIVER_INTENT);
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        myLog("");
        try {
            doWakefulWork(intent);
            /*
             * added sleep() because when RWI was run with no other app keeping
             * the phone awake, the wakelock releases too quickly and the sounds
             * are cut off.
             */
            SystemClock.sleep(3000);
        } finally {
            PowerManager.WakeLock lock = getLock(this.getApplicationContext());
            if (lock.isHeld()) {
                lock.release();
            }
        }
    }
}
