package com.juniorisep.backofficeJE.service.notification;

import com.google.firebase.messaging.*;
import com.juniorisep.backofficeJE.config.Constants;
import com.juniorisep.backofficeJE.domain.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageSource messageSource;

    public void sendMessage(Position position, NotificationStatus notificationStatus)
        throws InterruptedException, ExecutionException {
        for (String language : Constants.AVAILABLE_LANGUAGES) {
            Message message = getPreconfiguredMessage(position, notificationStatus, language);
            String response = sendAndGetResponse(message);
            log.info("Sent message Topic: " + Locale.forLanguageTag(language).getLanguage() + position.getJobType().getName() + ", " + response);
        }
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(Position position, NotificationStatus notificationStatus, String language) {
        String beginBody = messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text1", null, Locale.forLanguageTag(language));
        String endBody = messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text2", null, Locale.forLanguageTag(language));
        log.info("Send notification " + Locale.forLanguageTag(language).getLanguage() + ", message: " + beginBody + " " + endBody);
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis())
            .putData("positionId", position.getId().toString())
            .setNotification(AndroidNotification.builder()
                .setTitle(position.getMission().getCompany().getName())
                .setBody(String.format("%s %s %s",
                    beginBody,
                    position.getJobType().getName(),
                    endBody
                ))
                .build())
            .setCollapseKey(position.getJobType().getId().toString())
            .setPriority(AndroidConfig.Priority.HIGH)
            .build();
    }

    private ApnsConfig getApnsConfig(Position position, NotificationStatus notificationStatus, String language) {
        return ApnsConfig.builder()
            .putCustomData("positionId", position.getId())
            .setAps(Aps.builder().setCategory(position.getJobType().getId().toString()).setSound("default")
                .setAlert(ApsAlert.builder()
                    .setTitle(position.getMission().getCompany().getName())
                    .setBody(String.format("%s %s %s",
                        messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text1", null, Locale.forLanguageTag(language)),
                        position.getJobType().getName(),
                        messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text2", null, Locale.forLanguageTag(language))
                        ))
                    .build())
                .build())
            .build();
    }


    private Message getPreconfiguredMessage(Position position, NotificationStatus notificationStatus, String language) {
        return getPreconfiguredMessageBuilder(position, notificationStatus, language)
            .setTopic(language + position.getJobType().getId().toString())
            .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(Position position, NotificationStatus notificationStatus, String language) {
        AndroidConfig androidConfig = getAndroidConfig(position, notificationStatus, language);
        ApnsConfig apnsConfig = getApnsConfig(position, notificationStatus, language);
        return Message.builder()
            .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig);
    }

}
