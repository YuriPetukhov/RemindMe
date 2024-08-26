package yuri.petukhov.reminder.handling.creator;

import yuri.petukhov.reminder.business.dto.CommandEntity;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.enums.RoleName;

import java.util.List;

public interface MenuMessageCreator {

    void createInputWordMessage(Long chatId);

    void createInputMeaningMessage(CommandEntity commandEntity);

    void createCardSavedMessage(CommandEntity commandEntity);

    void createNotificationToUser(Long chatId, Long userId, String cardMeaning, int wordsNumber, ReminderInterval interval);

    void createOkMessage(Long chatId, Long userId);

    void createNoMessage(Long chatId, String cardName, Long userId);

    void createCompletedMessage(Long chatId);

    void createMenuMessage(Long chatId);

    void createLinkMessage(Long chatId, Long userId, List<String> roles);

    void createAdminNewUserNotifyMessage(String userName, Long chatId, Long chatId1);

    String getLatestMessage(Long chatId);

    void removeLatestMessage(Long chatId);

    void createGroupJoinCodeMessage(Long chatId);

    void createNoSuchGroupMessage(Long chatId);

    void createYouAddedMessage(Long chatId, String studentName);

    void createFirstNameMessage(Long chatId);

    void createLastNameMessage(Long chatId);

    void createAlreadyAddedMessage(Long chatId);

    void createNoNewWordsMessage(Long chatId);
}
