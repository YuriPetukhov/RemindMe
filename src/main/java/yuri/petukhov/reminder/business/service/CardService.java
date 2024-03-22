package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.handling.entity.CommandEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CardService {
    void createNewCard(String word, User user);

    void addMeaningToNewCard(Card card, String meaning);

    List<Card> findCardsForRecallMode();

    List<Card> findCardsInReminderInterval(LocalDateTime now, LocalDateTime end);

    void setRecallMode(List<Card> cards, RecallMode recallMode);
    void setRecallMode(Card card, RecallMode recallMode);

    Optional<Card> findActiveCardByUserId(Long userId);

    void setActivity(Card card, CardActivity cardActivity);

    void save(Card card);

    void setReminderInterval(Card card, LocalDateTime time, ReminderInterval interval);

    Optional<Card> findCardForRecallMode(Long userId);

    void deleteCard(Card card);
}
