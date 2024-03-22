package yuri.petukhov.reminder.bot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.UserRole;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final UpdateDispatcher updateDispatcher;
    private final UserService userService;
    private final TelegramBot telegramBot;
    public void dispatch(Update update) {
        if (update == null) return;

        Message message = update.message();
        Long chatId;
        Long userId;
        String userName;
        UserRole userRole;
        UserCardInputState cardState;

        if (message != null) {

            chatId = message.chat().id();
            userName = message.chat().firstName();
            userRole = userService.getUserState(chatId, userName);
            cardState = userService.getUserCardState(chatId);
            userId = userService.getUserId(chatId);
            updateDispatcher.handleIncomingMessage(message, chatId, userId, userRole, cardState);
        }

    }

}
