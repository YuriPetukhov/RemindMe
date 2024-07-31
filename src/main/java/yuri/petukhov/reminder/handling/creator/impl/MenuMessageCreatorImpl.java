package yuri.petukhov.reminder.handling.creator.impl;

import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.bot.configuration.RemindMeBotConfiguration;
import yuri.petukhov.reminder.bot.executor.MessageExecutor;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;
import yuri.petukhov.reminder.business.dto.CommandEntity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuMessageCreatorImpl implements MenuMessageCreator {
    private final MessageExecutor messageExecutor;
    private final RemindMeBotConfiguration configuration;
    private final SimpMessagingTemplate messagingTemplate;

    private String message;

    private ConcurrentMap<Long, String> latestMessages;

    @PostConstruct
    public void init() {
        latestMessages = new ConcurrentHashMap<>();
    }

    @Override
    public void createInputWordMessage(Long chatId) {
        log.info("Message about adding a new word was sent");
        String message = "Add your word";
        messageExecutor.executeMessage(message, chatId);
    }

    @Override
    public void createInputMeaningMessage(CommandEntity commandEntity) {
        log.info("Message about adding a description was sent");
        message = "Add word's description";
        messageExecutor.executeMessage(message, commandEntity.getChatId());
    }

    @Override
    public void createCardSavedMessage(CommandEntity commandEntity) {
        log.info("Message about adding a new card was sent");
        message = "Your card is added";
        messageExecutor.executeMessage(message, commandEntity.getChatId());
    }

    @Override
    public void createNotificationToUser(Long chatId, Long userId, String cardMeaning, int wordsNumber, ReminderInterval interval) {
        log.info("Notification {} was sent for chatId = {}", cardMeaning, chatId);

        String botMessage = formatMessage(cardMeaning, wordsNumber, interval, false);
        String webMessage = formatMessage(cardMeaning, wordsNumber, interval, true);

        messageExecutor.executeMessage(botMessage, chatId);
        messagingTemplate.convertAndSend("/topic/recall", webMessage);

        latestMessages.put(userId, webMessage);
    }

    @Override
    public void createOkMessage(Long chatId, Long userId) {
        log.info("Ok Message was sent for chatId = {}", chatId);
        message = "Yes! It is correct!";
        messageExecutor.executeMessage(message, chatId);
        messagingTemplate.convertAndSend("/topic/recall", message);
        removeLatestMessage(userId);
    }

    @Override
    public void createNoMessage(Long chatId, String cardName, Long userId) {
        log.info("No Message was sent for chatId = {}", chatId);
        message = "No! The word is " + cardName;
        messageExecutor.executeMessage(message, chatId);
        messagingTemplate.convertAndSend("/topic/recall", message);
        removeLatestMessage(userId);
    }

    @Override
    public void createCompletedMessage(Long chatId) {
        log.info("Completed Message was sent for chatId = {}", chatId);
        message = "Bingo! You have finished";
        messageExecutor.executeMessage(message, chatId);
        messagingTemplate.convertAndSend("/topic/recall", message);
    }

    @Override
    public void createMenuMessage(Long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId), configuration.getMENU_MES()).parseMode(HTML);
        messageExecutor.executeMessage(message);
    }

    @Override
    public void createLinkMessage(Long chatId, Long userId, List<String> roles) {
        String messageText = String.format(configuration.getWEB_LINK(), userId, userId, chatId);
        SendMessage message = new SendMessage(String.valueOf(chatId), messageText).parseMode(HTML);

        if(roles.contains(RoleName.ROLE_ADMIN.name())) {
            String messageTextAdmin = String.format(configuration.getWEB_LINK_ADMIN(), userId, userId, chatId);
            SendMessage messageAdmin = new SendMessage(String.valueOf(chatId), messageTextAdmin).parseMode(HTML);
            messageExecutor.executeMessage(messageAdmin);
        }
            messageExecutor.executeMessage(message);
    }

    @Override
    public void createAdminNewUserNotifyMessage(String userName, Long userChatId, Long adminChatId) {
        message = "New user: " + userName + ", chatId: " + userChatId;
        messageExecutor.executeMessage(message, adminChatId);
    }

    @Override
    public String getLatestMessage(Long userId) {
        return latestMessages.get(userId);
    }

    @Override
    public void removeLatestMessage(Long userId) {
        latestMessages.remove(userId);
    }

    private String formatMessage(String cardMeaning, int wordsNumber, ReminderInterval interval, boolean isForWeb) {
        String lineBreak = isForWeb ? "<br>" : "\n";
        return "Questions remain: " + wordsNumber + lineBreak + "Current interval: " + interval + lineBreak + cardMeaning;
    }

}
