package yuri.petukhov.reminder.business.service;

public interface RecallService {
    void recallWords();

    void activateRecallMode();

    void recallWordsForUser(Long userId);
}
