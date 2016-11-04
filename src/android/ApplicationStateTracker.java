package org.apache.cordova.firebase;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import static android.app.Application.ActivityLifecycleCallbacks;

/**
 * ApplicationStateTracker tracks the state of the application by implementing the ActivityLifecycleCallbacks
 * and tracking the number of started and stopped activities utilizing its onActivityStarted and onActivityStopped
 * callbacks
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ApplicationStateTracker implements ActivityLifecycleCallbacks {
    private int started;
    private int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * If the number of stopped activities is the same as the number of started activities this means that the
     * application is in background. This is true even if the application is started from a BroadcastReceiver
     * because in that case both counters will be set to zero.
     *
     * @return is the application in background
     */
    public boolean isInBackground() {
        return stopped == started;
    }
}
