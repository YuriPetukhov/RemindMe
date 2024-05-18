package yuri.petukhov.reminder.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.UserRole;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.handling.handler.CommandHandler;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateDispatcher {
    private final CommandHandler commandHandler;
    private final TelegramBot telegramBot;

    public void handleIncomingMessage(Message message, Long chatId, Long userId, UserRole userRole, UserCardInputState cardState) {
        String messageText = message.text();
        CommandEntity commandEntity = new CommandEntity();
        if (messageText != null) {
            commandEntity.setMessageText(messageText);
            commandEntity.setChatId(chatId);
            commandEntity.setUserId(userId);
            commandEntity.setUserRole(userRole);
            commandEntity.setCardState(cardState);
            commandHandler.handle(commandEntity);

        }
    }

}

