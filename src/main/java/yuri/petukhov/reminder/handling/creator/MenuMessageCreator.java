package yuri.petukhov.reminder.handling.creator;

import yuri.petukhov.reminder.business.dto.CommandEntity;

public interface MenuMessageCreator {

    void createInputWordMessage(Long chatId);

    void createInputMeaningMessage(CommandEntity commandEntity);

    void createCardSavedMessage(CommandEntity commandEntity);

    void createNotificationToUser(Long chatId, String cardMeaning, int wordsNumber);

    void createOkMessage(Long chatId);

    void createNoMessage(Long chatId, String cardName);

}
