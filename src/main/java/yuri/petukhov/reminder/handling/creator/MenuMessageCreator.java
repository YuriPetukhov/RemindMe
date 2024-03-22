package yuri.petukhov.reminder.handling.creator;

import yuri.petukhov.reminder.handling.entity.CommandEntity;

public interface MenuMessageCreator {

    void createInputWordMessage(Long chatId);

    void createInputMeaningMessage(CommandEntity commandEntity);

    void createCardSavedMessage(CommandEntity commandEntity);

    void createNotificationToUser(Long chatId, String cardMeaning);

    void createOkMessage(Long chatId);

    void createNoMessage(Long chatId, String cardName);

}
