package yuri.petukhov.reminder.business.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void notifyAdminOfNewUser(String userName, Long chatId) {
        String message = String.format("Новый пользователь: %s, номер чата: %d", userName, chatId);
        log.info("Message to the admin about a new user");
        kafkaTemplate.send("user-added-topic", message);
    }

}

