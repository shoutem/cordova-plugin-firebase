package org.apache.cordova.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class OnNotificationOpenReceiver extends BroadcastReceiver {
    private static final String TAG = OnNotificationOpenReceiver.class.getSimpleName();
    private static final String LAUNCH_NOTIFICATION = "launchNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(context.getPackageName());

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = intent.getExtras();

        FirebasePlugin.onNotificationOpen(data);
        launchIntent.putExtras(data);

        if (applicationIsInBackground(context)) {
            JSONObject receivedNotification = getJsonFromBundle(data);

            try {
                JSONObject notificationObject = new NotificationObject.Builder()
                        .withId(receivedNotification.getString("id"))
                        .withTitle(receivedNotification.getString("title"))
                        .withText(receivedNotification.getString("text"))
                        .withNotificationData(receivedNotification)
                        .withApplicationActive(false)
                        .withOpenedFromNotification(true)
                        .build()
                        .getJSON();

                launchIntent.putExtra(LAUNCH_NOTIFICATION, notificationObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse the received notification as JSON: " + e.getMessage());
            }
        }

        context.startActivity(launchIntent);
    }

    @NonNull
    private JSONObject getJsonFromBundle(Bundle data) {
        JSONObject receivedNotification = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                receivedNotification.put(key, data.get(key));
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse the notification bundle as JSON: " + e.getMessage());
            }
        }
        return receivedNotification;
    }

    private boolean applicationIsInBackground(Context context) {
        StateTrackableApplication application = (StateTrackableApplication) context.getApplicationContext();
        return application.getStateTracker().isInBackground();
    }
}
