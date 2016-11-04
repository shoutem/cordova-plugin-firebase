package org.apache.cordova.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class FirebasePluginMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebasePlugin";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        String title;
        String text;
        String id;
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            text = remoteMessage.getNotification().getBody();
            id = remoteMessage.getMessageId();
        } else {
            title = remoteMessage.getData().get("title");
            text = remoteMessage.getData().get("text");
            id = remoteMessage.getData().get("id");
        }
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message id: " + id);
        Log.d(TAG, "Notification Message Title: " + title);
        Log.d(TAG, "Notification Message Body/Text: " + text);

        Boolean isInBackground = true;
        Context context = getApplicationContext();
        if (context instanceof StateTrackableApplication) {
            StateTrackableApplication application = (StateTrackableApplication) getApplicationContext();
            isInBackground = application.getStateTracker().isInBackground();
        }

        if (!isInBackground) {
            sendNotificationToCordova(id, title, text, remoteMessage.getData());
        } else if (!TextUtils.isEmpty(text) || !TextUtils.isEmpty(title) || !TextUtils.isEmpty(id)) {
            // TODO: Add option to developer to configure if show notification when app on foreground
            sendNotification(id, title, text, remoteMessage.getData());
        }
    }

    private void sendNotificationToCordova(String id, String title, String text, Map<String, String> data) {
        JSONObject cordovaObject;
        JSONObject jsonData = new JSONObject(data);

        try {
            cordovaObject = new NotificationObject.Builder()
                    .withNotificationData(jsonData)
                    .withTitle(title)
                    .withText(text)
                    .withId(id)
                    .withApplicationActive(true)
                    .withOpenedFromNotification(false)
                    .build()
                    .getJSON();

            NotificationReceivedCallback.getInstance().call(cordovaObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to create a JSON object" + e.getMessage());
        }
    }

    private void sendNotification(String id, String title, String messageBody, Map<String, String> data) {
        Intent intent = new Intent(this, OnNotificationOpenReceiver.class);
        Bundle bundle = new Bundle();
        for (String key : data.keySet()) {
            bundle.putString(key, data.get(key));
        }
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id.hashCode(), notificationBuilder.build());
    }
}
