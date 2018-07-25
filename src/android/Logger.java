package org.apache.cordova.firebase;

import android.util.Log;
import org.apache.cordova.BuildConfig;

/**
 * Logger is an Android log wrapper which automatically turns off Logging for the production environment
 */
public enum Logger {
    INSTANCE;

    public void log(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
