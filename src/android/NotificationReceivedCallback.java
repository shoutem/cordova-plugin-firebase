package org.apache.cordova.firebase;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

/**
 * NotificationReceivedCallback is a singleton storing the callbackContext of the JavaScript method which can be called
 * from the native code at any time
 */
public class NotificationReceivedCallback {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    private static NotificationReceivedCallback instance = new NotificationReceivedCallback();

    public static NotificationReceivedCallback getInstance() {
        return instance;
    }

    private NotificationReceivedCallback() {
        /**
         * Use a NullObject implementation as the default to prevent the Object being in a state
         * where the null pointer exception could occur
         */
        callbackContext = new NullCallbackContext();
    }

    private class NullCallbackContext extends CallbackContext {

        private final String TAG = this.getClass().getSimpleName();

        public NullCallbackContext() {
            super(null, null);
        }

        /**
         * Log that the NotificationReceivedCallback is in an unusable state, but do not break the functionality
         *
         * @param pluginResult unused
         */
        @Override
        public void sendPluginResult(PluginResult pluginResult) {
            Logger.INSTANCE.log(TAG, "No notification receive callback context has been set");
        }
    }

    /**
     * Call the stored callback with the notificationObject as a parameter
     *
     * @param notificationObject to be passed as an argument to the callback
     */
    public void call(JSONObject notificationObject) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, notificationObject);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
}
