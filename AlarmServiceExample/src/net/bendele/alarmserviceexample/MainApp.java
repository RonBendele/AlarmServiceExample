package net.bendele.alarmserviceexample;

import android.app.Application;

public class MainApp extends Application {

    private static boolean globalScope = false;

    protected static boolean isScopeGlobal() {
        return globalScope;
    }

    protected static void setScopeGlobal(boolean global) {
        globalScope = global;
    }
}
