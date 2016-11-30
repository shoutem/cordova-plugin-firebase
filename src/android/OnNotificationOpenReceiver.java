package org.apache.cordova.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class OnNotificationOpenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(context.getPackageName());

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = intent.getExtras();

        FirebasePlugin.onNotificationOpen(data);
        launchIntent.putExtras(data);
        context.startActivity(launchIntent);
    }
}
