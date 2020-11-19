package com.mycompany.myapp.service.notification;

public enum NotificationParameter {
    SOUND("default"),
    COLOR("#1A243B");

    private final String value;

    NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
