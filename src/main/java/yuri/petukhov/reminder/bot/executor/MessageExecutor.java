package yuri.petukhov.reminder.bot.executor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class MessageExecutor {
    private final TelegramBot telegramBot;
    private SendMessage sendMessage;

    public void executeMessage(String message, Long chatId) {
        log.info("Message {} executing to user {}", message, chatId);
        sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }
}
