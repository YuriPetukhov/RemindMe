package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.handling.entity.CommandEntity;

public interface RecallService {
    void recallWords();

    void activateRecallMode();

    void recallWordsForUser(Long userId);
}
