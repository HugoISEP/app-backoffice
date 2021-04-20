package com.mycompany.myapp.service.notification;

import com.google.firebase.messaging.*;
import com.mycompany.myapp.domain.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.mycompany.myapp.config.Constants.AVAILABLE_LANGUAGES;

@Slf4j
@Service
public class NotificationService {

    private final MessageSource messageSource;

    public NotificationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void sendMessage(Position position, NotificationStatus notificationStatus)
        throws InterruptedException, ExecutionException {
        for (String language : AVAILABLE_LANGUAGES) {
            Message message = getPreconfiguredMessage(position, notificationStatus, language);
            String response = sendAndGetResponse(message);
            log.info("Sent message Topic: " + language + position.getJobType().getName() + ", " + response);
        }
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(Position position, NotificationStatus notificationStatus, String language) {
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis())
            .setNotification(AndroidNotification.builder()
                .setTitle(position.getMission().getCompany().getName())
                .setBody(String.format("%s %s %s",
                    messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text1", null, Locale.forLanguageTag(language)),
                    position.getJobType().getName(),
                    messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text2", null, Locale.forLanguageTag(language))
                ))
                .build())
            .setCollapseKey(position.getJobType().getId().toString())
            .setPriority(AndroidConfig.Priority.HIGH)
            .build();
    }

    private ApnsConfig getApnsConfig(Position position, NotificationStatus notificationStatus, String language) {
        return ApnsConfig.builder()
            .setAps(Aps.builder().setCategory(position.getJobType().getId().toString()).setSound("default")
                .setAlert(ApsAlert.builder()
                    .setTitle(position.getMission().getCompany().getName())
                    .setBody(String.format("%s %s %s",
                        messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text1", null, Locale.forLanguageTag(language)),
                        position.getJobType().getName(),
                        messageSource.getMessage("mission_status." + notificationStatus.getValue() + ".text2", null, Locale.forLanguageTag(language))
                        ))
                    .build())
                .setContentAvailable(true)
                .build())
            .putHeader("apns-priority", "10")
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
