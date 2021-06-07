package com.juniorisep.backofficeJE.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationParameter {
    SOUND("default"),
    COLOR("#1A243B");

    private final String value;
}
