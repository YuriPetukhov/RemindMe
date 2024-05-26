package yuri.petukhov.reminder.handling.creator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.bot.executor.MessageExecutor;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;
import yuri.petukhov.reminder.business.dto.CommandEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuMessageCreatorImpl implements MenuMessageCreator {
    private final MessageExecutor messageExecutor;
    private String message;

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
    public void createNotificationToUser(Long chatId, String cardMeaning, int wordsNumber) {
        log.info("Notification {} was sent for chatId = {}", cardMeaning, chatId);
        messageExecutor.executeMessage("Questions remain: " + wordsNumber + "\n" + cardMeaning, chatId);
    }

    @Override
    public void createOkMessage(Long chatId) {
        log.info("Ok Message was sent for chatId = {}", chatId);
        message = "Yes!";
        messageExecutor.executeMessage(message, chatId);
    }

    @Override
    public void createNoMessage(Long chatId, String cardName) {
        log.info("No Message was sent for chatId = {}", chatId);
        message = "No! The word is " + cardName;
        messageExecutor.executeMessage(message, chatId);
    }

}
