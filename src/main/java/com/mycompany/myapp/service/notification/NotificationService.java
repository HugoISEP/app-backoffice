package com.mycompany.myapp.service.notification;

import com.google.firebase.messaging.*;
import com.mycompany.myapp.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    public void sendMessage(Position position, NotificationStatus notificationStatus)
        throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessage(position, notificationStatus);
        String response = sendAndGetResponse(message);
        logger.info("Sent message Topic: " + position.getJobType().getName() + ", " + response);
    }


    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis())
            .setCollapseKey(topic)
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue()).setIcon("ic_stat")
                .setColor(NotificationParameter.COLOR.getValue()).setTag(topic).build())
            .build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        HashMap<String, String> map = new HashMap<>();
        map.put("apns-priority", "10");
        return ApnsConfig.builder()
            .setAps(Aps.builder().setCategory(topic).setSound("default")
                .setContentAvailable(true)
                .setThreadId(topic).build())
            .putAllHeaders(map)
            .build();
    }


    private Message getPreconfiguredMessage(Position position, NotificationStatus notificationStatus) {
        return getPreconfiguredMessageBuilder(position, notificationStatus).setTopic(position.getJobType().getId().toString())
            .putData("title", position.getMission().getCompany().getName()).putData("body", position.getJobType().getName())
            .putData("status", notificationStatus.getValue())
            .build();
    }


    private Message.Builder getPreconfiguredMessageBuilder(Position position, NotificationStatus status) {
        AndroidConfig androidConfig = getAndroidConfig(position.getJobType().getId().toString());
        ApnsConfig apnsConfig = getApnsConfig(position.getJobType().getId().toString());
        //TODO Clean this
        if (status.equals(NotificationStatus.NEW)){
            return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                    Notification.builder()
                        .setTitle(position.getMission().getCompany().getName())
                        .setBody("Une nouvelle mission " + position.getJobType().getName() +  " est disponible !").build());
            //Notification class doesn't even have setters :/
        }
        else {
            return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                    Notification.builder()
                        .setTitle(position.getMission().getCompany().getName())
                        .setBody("Une mission " + position.getJobType().getName() + " est toujours disponible !").build());
        }
    }

}
