package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.service.CardService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @Override
    public void createNewCard(String word, User user) {
        log.info("Starting the creation of a new card for {}", word);
        Card card = Card.createCard(word, user);
        cardRepository.save(card);
    }

    @Override
    public void addMeaningToNewCard(Card card, String meaning) {
        log.info("Starting adding of a meaning {} to the new card", meaning);
        card.setCardMeaning(meaning);
        cardRepository.save(card);
    }


    @Override
    public List<Card> findCardsForRecallMode() {
        log.info("findCardsForRecallMode() is started");
        return cardRepository.findDistinctRecallCardsByUser();
    }

    @Override
    public List<Card> findCardsInReminderInterval(LocalDateTime now, LocalDateTime end) {
        log.info("findCardsInReminderInterval() is started");
        return cardRepository.findAllByReminderDateTimeBetween(now, end);

    }

    @Override
    public void setRecallMode(List<Card> cards, RecallMode recallMode) {
        log.info("setRecallMode() is started");
        for (Card card : cards) {
            card.setRecallMode(recallMode);
        }
        cardRepository.saveAll(cards);
    }

    @Override
    public void setRecallMode(Card card, RecallMode recallMode) {
        log.info("setRecallMode() is started");
        card.setRecallMode(recallMode);
        cardRepository.save(card);

    }

    @Override
    public Optional<Card> findActiveCardByUserId(Long userId) {
        log.info("findActiveCardByUserId() is started");
        return cardRepository.findActiveCardByUserId(userId);
    }

    @Override
    public void setActivity(Card card, CardActivity cardActivity) {
        log.info("setActivity() is started");
        card.setActivity(cardActivity);
        cardRepository.save(card);
    }

    @Override
    public void save(Card card) {
        log.info("save() is started");
        cardRepository.save(card);
    }

    @Override
    public void setReminderInterval(Card card, LocalDateTime time, ReminderInterval interval) {
        log.info("setReminderInterval() is started");
        card.setInterval(interval);
        card.setReminderDateTime(time);
        cardRepository.save(card);
    }

    @Override
    public Optional<Card> findCardForRecallMode(Long userId) {
        log.info("findCardForRecallMode() is started");
        return cardRepository.findFirstByUserIdAndRecallMode(userId, RecallMode.RECALL);
    }

    @Override
    public void deleteCard(Card card) {
        cardRepository.delete(card);
    }
}
