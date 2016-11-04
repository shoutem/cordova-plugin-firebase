package org.apache.cordova.firebase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NotificationObject is a class representing the object which can be serialized to cordova-plugin-parse
 * format.
 */
public class NotificationObject {
    private String id;
    private JSONObject notificationData;
    private String text;
    private String title;
    private boolean isActive = false;
    private boolean isOpenedFromNotification = false;

    public static class Builder {
        private String id;
        private JSONObject notificationData;
        private String title;
        private String text;
        private boolean isActive;
        private boolean isOpenedFromNotification;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withNotificationData(JSONObject notificationData) {
            this.notificationData = notificationData;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withApplicationActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder withOpenedFromNotification(boolean isOpenedFromNotification) {
            this.isOpenedFromNotification = isOpenedFromNotification;
            return this;
        }

        public NotificationObject build() {
            return new NotificationObject(id, notificationData, title, text, isActive, isOpenedFromNotification);
        }
    }

    private NotificationObject(String id, JSONObject notificationData, String title, String text, boolean isActive, boolean isOpenedFromNotification) {
        this.id = id;
        this.notificationData = notificationData;
        this.title = title;
        this.text = text;
        this.isActive = isActive;
        this.isOpenedFromNotification = isOpenedFromNotification;
    }

    /**
     * Serialize the notificationObject to the standard, platform-agnostic cordova-plugin-parse format
     *
     * @return platform-agnostic notificationObject
     * @throws JSONException
     */
    public JSONObject getJSON() throws JSONException {
        JSONObject notificationObject = new JSONObject();
        JSONObject notification = new JSONObject();

        notification.put("id", id);
        notification.put("extras", notificationData);
        notification.put("title", title);
        notification.put("text", text);

        notificationObject.put("notification", notification);
        notificationObject.put("applicationState", getApplicationStateJson());

        return notificationObject;
    }

    private JSONObject getApplicationStateJson() throws JSONException {
        JSONObject applicationState = new JSONObject();

        applicationState.put("active", isActive);
        applicationState.put("openedFromNotification", isOpenedFromNotification);

        return applicationState;
    }
}

