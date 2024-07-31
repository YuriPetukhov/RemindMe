package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.service.CardService;
import yuri.petukhov.reminder.business.service.RecallService;
import yuri.petukhov.reminder.business.service.UserService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing recall operations on cards.
 * This class provides methods to activate recall mode, recall words for a specific user, and recall words for all users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecallServiceImpl implements RecallService {
    private final CardService cardService;
    private final UserService userService;
    private final MenuMessageCreator menuMessageCreator;

    /**
     * Activates recall mode for cards that are due for review.
     * This method finds cards within the reminder interval and sets them to recall mode.
     */
    @Override
    @Transactional
    public void activateRecallMode() {
        log.info("activateRecallMode() is started");
        LocalDateTime recallTime = LocalDateTime.now().plusMinutes(5);
        List<Card> cards = cardService.findCardsInReminderInterval(recallTime);
        if (!cards.isEmpty()) {
            cardService.setRecallMode(cards, RecallMode.RECALL);
        }
    }

    /**
     * Initiates the recall process for a specific user's cards.
     * This method finds a card for recall, updates the user's input state, and sends a notification to the user.
     * @param userId The ID of the user to recall words for.
     */
    @Transactional
    @Override
    public void recallWordsForUser(Long userId) {
        log.info("recallWordsForUser() is started");
        Optional<Card> cardOpt = cardService.findCardForRecallMode(userId);
        User user = userService.findUserById(userId);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            userService.setCardInputState(user, UserCardInputState.ANSWER);
            cardService.activateCard(card, userId);
            int wordsNumber = cardService.getCardByRecallMode(user.getId(), RecallMode.RECALL).size();
            menuMessageCreator.createNotificationToUser(user.getChatId(), userId, card.getCardMeaning(), wordsNumber, card.getInterval());
        } else{
            userService.setCardInputState(user, UserCardInputState.NONE);
        }
    }

    /**
     * Initiates the recall process for all users with cards in recall mode.
     * This method iterates through all users with recall cards and calls recallWordsForUser for each.
     */

    @Override
    public void recallWords() {
        log.info("recallWords() is started");
        List<User> usersWithRecallCards = cardService.findUsersForRecallMode();
        for (User user : usersWithRecallCards) {
            recallWordsForUser(user.getId());
        }
        log.info("recallWords() is completed");
    }
}
