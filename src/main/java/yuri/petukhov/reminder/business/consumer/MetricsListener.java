package yuri.petukhov.reminder.business.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.bot.executor.MessageExecutor;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.service.AdminService;
import yuri.petukhov.reminder.business.service.MetricService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class MetricsListener {

    private final AdminService adminService;
    private final MessageExecutor messageExecutor;
    private final MetricService metricService;

    @KafkaListener(topics = "user-added-topic", groupId = "group_id")
    public void listenForNewUser(String message) {
        List<User> admins = adminService.findAdmins();
        for (User admin : admins) {
            if (admin.getCardState().equals(UserCardInputState.NONE)) {
                messageExecutor.executeMessage(message, admin.getChatId());
            } else {
                metricService.saveMetric(message, admin.getChatId());
            }
        }
    }
}


