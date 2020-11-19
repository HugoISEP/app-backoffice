package com.mycompany.myapp.service.notification;

import com.google.firebase.messaging.*;
import com.mycompany.myapp.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    public void sendMessage(Position position)
        throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessage(position);
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
        return ApnsConfig.builder()
            .setAps(Aps.builder().setCategory(topic).setSound("default")
                .setContentAvailable(true)
                .setThreadId(topic).build())
            .build();
    }


    private Message getPreconfiguredMessage(Position position) {
        return getPreconfiguredMessageBuilder(position).setTopic(position.getJobType().getId().toString())
            .putData("title", position.getMission().getCompany().getName()).putData("body", "Une nouvelle mission "+position.getJobType().getName() + " est disponible !")
            .build();
    }


    private Message.Builder getPreconfiguredMessageBuilder(Position position) {
        AndroidConfig androidConfig = getAndroidConfig(position.getJobType().getId().toString());
        ApnsConfig apnsConfig = getApnsConfig(position.getJobType().getId().toString());
        return Message.builder()
            .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                new Notification(position.getMission().getCompany().getName(), "Une nouvelle mission "+position.getJobType().getName() + " est disponible !"));
    }

}
