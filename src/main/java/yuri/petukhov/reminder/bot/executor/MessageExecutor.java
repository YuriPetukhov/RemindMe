package yuri.petukhov.reminder.bot.executor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.service.UserService;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class MessageExecutor {
    private final TelegramBot telegramBot;
    private SendMessage sendMessage;
    private final SimpMessagingTemplate messagingTemplate;

    public void executeMessage(String message, Long chatId) {
        log.info("Message {} executing to user {}", message, chatId);
        sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
        messagingTemplate.convertAndSend("/topic/recall/" + chatId, "New message");
    }

    public void executeMessage(SendMessage message) {
        telegramBot.execute(message);
    }
}
