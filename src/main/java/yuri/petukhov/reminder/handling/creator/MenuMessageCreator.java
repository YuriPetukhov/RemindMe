package yuri.petukhov.reminder.handling.creator;

import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.enums.RoleName;

import java.util.List;

public interface MenuMessageCreator {

    void createInputWordMessage(Long chatId);

    void createInputMeaningMessage(CommandEntity commandEntity);

    void createCardSavedMessage(CommandEntity commandEntity);

    void createNotificationToUser(Long chatId, String cardMeaning, int wordsNumber, ReminderInterval interval);

    void createOkMessage(Long chatId);

    void createNoMessage(Long chatId, String cardName);

    void createCompletedMessage(Long chatId);

    void createMenuMessage(Long chatId);

    void createLinkMessage(Long chatId, Long userId, List<String> roles);

    void createAdminNewUserNotifyMessage(String userName, Long chatId, Long chatId1);
}
