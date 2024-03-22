package yuri.petukhov.reminder.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateListener {
    private final UserManager userManager;
    private final TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(updates -> {
            updates.stream()
                    .filter(this::isValidMessage)
                    .forEach(userManager::dispatch);
            updates.forEach(this::deleteMessage);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private boolean isValidMessage(Update update) {
        Message message = update.message();
        return message != null && message.text() != null && !message.text().isBlank();
    }

    private void deleteMessage(Update update) {
        if (update == null || update.message() == null) return;
        log.info("The message {} has ID {}", update.message().text(), update.message().messageId());
        Integer messageId = update.message().messageId();
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            telegramBot.execute(new DeleteMessage(update.message().chat().id(), messageId - 2));
            telegramBot.execute(new DeleteMessage(update.message().chat().id(), messageId - 1));
            telegramBot.execute(new DeleteMessage(update.message().chat().id(), messageId));
        }, 3, TimeUnit.SECONDS);
    }
}
