package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;

import java.time.LocalDateTime;
import java.util.List;
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

    List<Card> getAllCardsByUserId(Long userId, Integer pageNumber, Integer pageSize);

    List<Card> getAllCardsByUserIdAndReminderInterval(Long userId, ReminderInterval interval, Integer pageNumber, Integer pageSize);

    List<Card> getCardByName(Long userId, String cardName);

    Card updateCard(Long userId, Long cardId, CardUpdate updatedCard);

    void deleteCardById(Long cardId, Long userId);

    void addNewCard(CardUpdate card, Long userId);

    List<Card> getCardByCardActivity(Long userId, CardActivity activity);

    List<Card> getCardByRecallMode(Long userId, RecallMode mode);

    List<Card> getCardByReminderDateTime(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    List<Card> getCardNameDuplicates(Long userId);

    List<Card> getCardMeaningDuplicates(Long userId);

    Optional<Card> findById(Long cardId);
}
