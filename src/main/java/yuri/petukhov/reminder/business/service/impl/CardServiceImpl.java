package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.exception.CardNotFoundException;
import yuri.petukhov.reminder.business.mapper.CardMapper;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static yuri.petukhov.reminder.business.enums.CardActivity.INACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final UserService userService;

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
        log.info("setActivity() {} is started", cardActivity);
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

    @Override
    public List<Card> getAllCardsByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return cardRepository.findAllByUserId(userId, pageable).getContent();
    }


    @Override
    public List<Card> getAllCardsByUserIdAndReminderInterval(Long userId, ReminderInterval interval, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return cardRepository.findAllByUserIdAndReminderInterval(userId, interval, pageable).getContent();
    }

    @Override
    public List<Card> getCardByName(Long userId, String cardName) {
        return cardRepository.findAllByCardNameAndUserId(userId, cardName);
    }

    @Override
    public Card updateCard(Long userId, Long cardId, CardUpdate updatedCard) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " by user " + userId + " was not found"));
        return cardRepository.save(mapper.updateCard(card, updatedCard));
    }

    @Override
    public void deleteCardById(Long userId, Long cardId) {
        if (cardRepository.findCardByUserId(userId, cardId).isPresent()) {
            cardRepository.deleteById(cardId);
        } else {
            throw new CardNotFoundException("Card with id " + cardId + " by user " + userId + " was not found");
        }
    }

    @Override
    public void addNewCard(CardUpdate card, Long userId) {
        User user = userService.findUserById(userId);
        Card newCard = Card.createCard(card.getCardName(), user);
        newCard.setActivity(INACTIVE);
        newCard.setCardMeaning(card.getCardMeaning());
        cardRepository.save(mapper.updateCard(newCard, card));
    }

    @Override
    public List<Card> getCardByCardActivity(Long userId, CardActivity activity) {
        return cardRepository.findCardByCardActivity(userId, activity);
    }

    @Override
    public List<Card> getCardByRecallMode(Long userId, RecallMode mode) {
        return cardRepository.findCardByRecallMode(userId, mode);
    }

    @Override
    public List<Card> getCardByReminderDateTime(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return cardRepository.findCardByReminderDateTime(userId, startTime, endTime);
    }

    @Override
    public List<Card> getCardNameDuplicates(Long userId) {
        return cardRepository.findCardNameDuplicates(userId);
    }

    @Override
    public List<Card> getCardMeaningDuplicates(Long userId) {
        return cardRepository.findCardMeaningDuplicates(userId);
    }

    @Override
    public Integer getAllCardsNumberByUserIdAndReminderInterval(Long userId, ReminderInterval interval) {
        return cardRepository.findAllCardsNumberByUserIdAndReminderInterval(userId, interval);
    }
}
