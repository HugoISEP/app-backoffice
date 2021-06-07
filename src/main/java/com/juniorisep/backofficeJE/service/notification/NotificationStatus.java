package com.juniorisep.backofficeJE.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum NotificationStatus {
    NEW("new"),
    OLD("old");

    private final String value;
}
