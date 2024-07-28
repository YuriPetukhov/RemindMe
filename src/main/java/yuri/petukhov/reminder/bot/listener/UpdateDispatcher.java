package yuri.petukhov.reminder.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.handling.handler.CommandHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateDispatcher {
    private final CommandHandler commandHandler;

    public void handleIncomingMessage(Message message, Long chatId, Long userId, List<String> roles, UserCardInputState cardState) {
        String messageText = message.text();
        CommandEntity commandEntity = new CommandEntity();
        if (messageText != null && !messageText.trim().isEmpty()) {
            commandEntity.setMessageText(messageText);
            commandEntity.setChatId(chatId);
            commandEntity.setUserId(userId);
            commandEntity.setRoles(roles);
            commandEntity.setCardState(cardState);
            commandHandler.handle(commandEntity);
        }
    }

}

