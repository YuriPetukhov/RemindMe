package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.exception.CardNotFoundException;
import yuri.petukhov.reminder.business.mapper.CardMapper;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.CardRepository;
import yuri.petukhov.reminder.business.repository.MatchResultRepository;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static yuri.petukhov.reminder.business.enums.CardActivity.*;

/**
 * Service implementation for managing card-related operations.
 * This class provides methods to create, update, delete, and retrieve card information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final UserService userService;
    private final MatchResultRepository resultRepository;
    private final Object lock = new Object();

    /**
     * Creates a new card with the given word and associates it with the provided user.
     * @param word The word to create a card for.
     * @param user The user to associate the card with.
     */

    @Override
    public void createNewCard(String word, User user) {
        log.info("Starting the creation of a new card for {}", word);
        Card card = Card.createCard(word, user);
        cardRepository.save(card);
    }

    /**
     * Adds a meaning to a newly created card.
     * @param card The card to add the meaning to.
     * @param meaning The meaning to add to the card.
     */

    @Override
    public void addMeaningToNewCard(Card card, String meaning) {
        log.info("Starting adding of a meaning {} to the new card", meaning);
        card.setCardMeaning(meaning);
        cardRepository.save(card);
    }

    /**
     * Retrieves a list of users who are eligible for recall mode based on their cards.
     * @return A list of users for recall mode.
     */

    @Override
    public List<User> findUsersForRecallMode() {
        log.info("findUsersForRecallMode() is started");
        List<Card> cards = cardRepository.findDistinctRecallCardsByUserExcludingAnswer();
        return cards.stream()
                .map(Card::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Finds cards that are within the reminder interval up to the specified recall time.
     * @param recallTime The time before which cards should be recalled.
     * @return A list of cards within the reminder interval.
     */

    @Override
    public List<Card> findCardsInReminderInterval(LocalDateTime recallTime) {
        log.info("findCardsInReminderInterval() is started");
        return cardRepository.findAllByReminderDateTimeBeforeAndActivityNotAndRecallMode(recallTime, FINISHED, RecallMode.NONE);
    }

    /**
     * Sets the recall mode for a list of cards.
     * @param cards The list of cards to set the recall mode for.
     * @param recallMode The recall mode to set.
     */

    @Override
    public void setRecallMode(List<Card> cards, RecallMode recallMode) {
        log.info("setRecallMode() is started");
        for (Card card : cards) {
            card.setRecallMode(recallMode);
        }
        cardRepository.saveAll(cards);
    }

    /**
     * Sets the recall mode for a single card.
     * @param card The card to set the recall mode for.
     * @param recallMode The recall mode to be applied to the card.
     */

    @Override
    public void setRecallMode(Card card, RecallMode recallMode) {
        log.info("setRecallMode() is started");
        card.setRecallMode(recallMode);
        cardRepository.save(card);

    }

    /**
     * Finds the active card associated with a given user ID.
     * @param userId The ID of the user to find the active card for.
     * @return An Optional containing the active card if found.
     */

    @Override
    public Optional<Card> findActiveCardByUserId(Long userId) {
        log.info("findActiveCardByUserId() is started");
        return cardRepository.findActiveCardByUserId(userId);
    }

    /**
     * Sets the activity status of a card.
     * @param card The card to set the activity for.
     * @param cardActivity The activity status to be set for the card.
     */

    @Override
    public void setActivity(Card card, CardActivity cardActivity) {
        log.info("setActivity() {} is started", cardActivity);
        card.setActivity(cardActivity);
        cardRepository.save(card);
    }

    /**
     * Saves the card information to the repository.
     * @param card The card to be saved.
     */

    @Override
    public void save(Card card) {
        log.info("save() is started");
        cardRepository.save(card);
    }

    /**
     * Sets the reminder interval and time for a card.
     * @param card The card to set the reminder for.
     * @param time The time to set for the reminder.
     * @param interval The interval to set for the reminder.
     */

    @Override
    public void setReminderInterval(Card card, LocalDateTime time, ReminderInterval interval) {
        log.info("setReminderInterval() is started");
        card.setInterval(interval);
        card.setReminderDateTime(time);
        cardRepository.save(card);
    }

    /**
     * Finds the first card in recall mode for a given user ID.
     * @param userId The ID of the user to find the card for.
     * @return An Optional containing the card if found.
     */

    @Override
    public Optional<Card> findCardForRecallMode(Long userId) {
        log.info("findCardForRecallMode() is started");
        return cardRepository.findFirstByUserIdAndRecallMode(userId, RecallMode.RECALL);
    }

    /**
     * Deletes a card from the repository.
     * @param card The card to be deleted.
     */

    @Override
    public void deleteCard(Card card) {
        cardRepository.delete(card);
    }

    /**
     * Retrieves all cards for a user based on page number and size.
     * @param userId The ID of the user whose cards are to be retrieved.
     * @param pageNumber The page number for pagination.
     * @param pageSize The size of the page for pagination.
     * @return A list of CardDTOs for the specified page.
     */

    @Override
    public List<CardDTO> getAllCardsByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<Card> cards = cardRepository.findAllByUserId(userId, pageable).getContent();
        return cards.stream()
                .map(mapper::toCardDTO)
                .toList();
    }

    /**
     * Retrieves all cards for a user within a specific reminder interval.
     * @param userId The ID of the user whose cards are to be retrieved.
     * @param interval The reminder interval to filter the cards.
     * @param pageNumber The page number for pagination.
     * @param pageSize The size of the page for pagination.
     * @return A list of CardDTOs within the specified reminder interval.
     */

    @Override
    public List<CardDTO> getAllCardsByUserIdAndReminderInterval(Long userId, ReminderInterval interval, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<Card> cards = cardRepository.findAllByUserIdAndReminderInterval(userId, interval, pageable).getContent();
        return cards.stream()
                .map(mapper::toCardDTO)
                .toList();
    }

    /**
     * Retrieves a list of cards by name for a given user.
     * @param userId The ID of the user to search for cards.
     * @param cardName The name of the card to search for.
     * @return A list of FindCardDTOs matching the card name.
     */

    @Override
    public List<CardDTO> getCardByName(Long userId, String cardName) {
        List<Card> results = cardRepository.findAllByCardNameAndUserId(userId, cardName);
        return results.stream()
                .map(mapper:: toCardDTO)
                .toList();
    }

    /**
     * Updates a card's information based on the provided update data.
     * @param userId The ID of the user who owns the card.
     * @param cardId The ID of the card to be updated.
     * @param updatedCard The updated card information.
     * @return The updated Card object.
     */

    @Override
    public Card updateCard(Long userId, Long cardId, CardUpdate updatedCard) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " by user " + userId + " was not found"));
        return cardRepository.save(mapper.updateCard(card, updatedCard));
    }

    /**
     * Deletes a card by its ID, ensuring it belongs to the specified user.
     * @param userId The ID of the user who owns the card.
     * @param cardId The ID of the card to be deleted.
     */

    @Transactional
    @Override
    public void deleteCardById(Long userId, Long cardId) {
        if (cardRepository.findCardByUserId(userId, cardId).isPresent()) {
            resultRepository.deleteByCardId(cardId);
            cardRepository.deleteById(cardId);
        } else {
            throw new CardNotFoundException("Card with id " + cardId + " by user " + userId + " was not found");
        }
    }

    /**
     * Adds a new card with the provided information for a user.
     * @param card The information for the new card.
     * @param userId The ID of the user to add the new card for.
     */

    @Override
    public void addNewCard(CardUpdate card, Long userId) {
        User user = userService.findUserById(userId);
        Card newCard = Card.createCard(card.getTitle(), user);
        newCard.setActivity(INACTIVE);
        newCard.setCardMeaning(card.getContent());
        cardRepository.save(mapper.updateCard(newCard, card));
    }

    /**
     * Retrieves a list of cards for a user based on card activity status.
     * @param userId The ID of the user to retrieve cards for.
     * @param activity The activity status to filter the cards.
     * @return A list of Cards with the specified activity status.
     */

    @Override
    public List<Card> getCardByCardActivity(Long userId, CardActivity activity) {
        return cardRepository.findCardByCardActivity(userId, activity);
    }

    /**
     * Retrieves a list of cards for a user based on recall mode.
     * @param userId The ID of the user to retrieve cards for.
     * @param mode The recall mode to filter the cards.
     * @return A list of Cards in the specified recall mode.
     */

    @Override
    public List<Card> getCardByRecallMode(Long userId, RecallMode mode) {
        return cardRepository.findCardByRecallMode(userId, mode);
    }

    /**
     * Retrieves a list of cards for a user within a specific time range.
     * @param userId The ID of the user to retrieve cards for.
     * @param startTime The start time of the range.
     * @param endTime The end time of the range.
     * @return A list of Cards within the specified time range.
     */

    @Override
    public List<Card> getCardByReminderDateTime(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return cardRepository.findCardByReminderDateTime(userId, startTime, endTime);
    }

    /**
     * Finds duplicate card names for a given user.
     * @param userId The ID of the user to check for duplicate card names.
     * @return A list of Cards with duplicate names.
     */

    @Override
    public List<Card> getCardNameDuplicates(Long userId) {
        return cardRepository.findCardNameDuplicates(userId);
    }

    /**
     * Finds duplicate card meanings for a given user.
     * @param userId The ID of the user to check for duplicate card meanings.
     * @return A list of Cards with duplicate meanings.
     */
    @Override
    public List<Card> getCardMeaningDuplicates(Long userId) {
        return cardRepository.findCardMeaningDuplicates(userId);
    }

    /**
     * Counts all cards for a user within a specific reminder interval.
     * @param userId The ID of the user to count cards for.
     * @param interval The reminder interval to filter the cards.
     * @return The number of cards within the specified interval.
     */
    @Override
    public Integer getAllCardsNumberByUserIdAndReminderInterval(Long userId, ReminderInterval interval) {
        return cardRepository.findAllCardsNumberByUserIdAndReminderInterval(userId, interval);
    }

    /**
     * Retrieves a specific card for a user by card ID.
     * @param cardId The ID of the card to retrieve.
     * @return The requested Card object.
     */

    @Override
    public CardDTO getUserCardById(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException("Card with id " + cardId + " was not found"));

        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(cardId);
        cardDTO.setTitle(card.getCardName());
        cardDTO.setContent(card.getCardMeaning());
        cardDTO.setReminderDateTime(card.getReminderDateTime());

        return cardDTO;
    }

    /**
     * Activates a card for a user, ensuring that only one card is active at a time.
     * @param card The card to activate.
     * @param userId The ID of the user for whom the card is to be activated.
     */

    @Override
    public void activateCard(Card card, Long userId) {
        synchronized(lock) {
            Optional<Card> activeCard = findActiveCardByUserId(userId);
            if (activeCard.isPresent()) {
                log.info("Attempt to activate a second card for user: " + userId);
                return;
            }
            setActivity(card, ACTIVE);
        }
    }

    /**
     * Retrieves a list of card data transfer objects (DTOs) for a user.
     * @param userId The ID of the user whose cards are to be retrieved.
     * @return A list of CardDTOs for the user.
     */

    @Override
    public List<CardDTO> getAllCardsDTOByUserId(Long userId) {
        List<Card> cards = cardRepository.findRandomCardsByUserId(userId, 60);
        return cards.stream()
                .map(mapper::toCardDTO)
                .toList();
    }

    /**
     * Gathers statistics for all reminder intervals for a user.
     * @param userId The ID of the user whose statistics are to be gathered.
     * @return A list of integers representing the statistics for all intervals.
     */

    @Override
    public List<Integer> getStatsForAllIntervals(Long userId) {
        List<Integer> stats = new ArrayList<>();
        for (ReminderInterval interval : ReminderInterval.values()) {
            int cardsNumber = getAllCardsNumberByUserIdAndReminderInterval(userId, interval);
            stats.add(cardsNumber);
        }
        int allCardsNumber = cardRepository.getAllCardsNumber(userId);
        int finishedCardsNumber = cardRepository.getFinishedCardsNumber(userId, FINISHED);
        stats.add(allCardsNumber);
        stats.add(finishedCardsNumber);

        return stats;
    }

    /**
     * Retrieves a random card DTO for a user.
     * @param userId The ID of the user to retrieve a random card for.
     * @return A CardDTO representing the random card.
     */

    @Override
    public CardDTO getRandomCardsDTOByUserId(Long userId) {
        List<Card> cards = cardRepository.findRandomCardsByUserId(userId, 1);
        if (cards.isEmpty()) {
            return new CardDTO();
        }
        return mapper.toCardDTO(cards.get(0));
    }

    @Override
    public Card findById(Long cardId) {
        Optional<Card> optCard = cardRepository.findById(cardId);
        if(optCard.isPresent()) {
            return optCard.get();
        } else {
            throw new CardNotFoundException("Card " + cardId + " not found");
        }

    }

    @Override
    public List <Card> saveAll(List<Card> cardToSave) {
        return cardRepository.saveAll(cardToSave);
    }

    @SuppressWarnings("unused")
    public boolean isAuthorCard(String userId, Long cardId) {
        User user = userService.findUserById(Long.valueOf(userId));
        Card card = cardRepository.findById(cardId).orElse(null);
        return user != null && card != null && card.getUser().getId().equals(user.getId());
    }
}
