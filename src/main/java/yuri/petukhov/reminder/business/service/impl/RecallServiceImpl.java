package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.enums.CardActivity;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class RecallServiceImpl implements RecallService {
    private final CardService cardService;
    private final UserService userService;
    private final MenuMessageCreator menuMessageCreator;

    @Override
    @Transactional
    public void activateRecallMode() {
        log.info("activateRecallMode() is started");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(20);
        List<Card> cards = cardService.findCardsInReminderInterval(now, end);
        if (!cards.isEmpty()) {
            cardService.setRecallMode(cards, RecallMode.RECALL);
        }
    }

    @Override
    public void recallWordsForUser(Long userId) {
        log.info("recallWordsForUser() is started");
        Optional<Card> cardOpt = cardService.findCardForRecallMode(userId);
        User user = userService.findUserById(userId);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            userService.setCardInputState(user, UserCardInputState.ANSWER);
            cardService.setActivity(card, CardActivity.ACTIVE);
            int wordsNumber = cardService.getCardByRecallMode(user.getId(), RecallMode.RECALL).size();
            menuMessageCreator.createNotificationToUser(user.getChatId(), card.getCardMeaning(), wordsNumber, card.getInterval());
        } else{
            userService.setCardInputState(user, UserCardInputState.NONE);
        }
    }

    @Override
    public void recallWords() {
        log.info("recallWords() is started");
        List<Card> cards = cardService.findCardsForRecallMode();
        if (!cards.isEmpty()) {
            for (Card card : cards) {
                User user = card.getUser();
                Optional<Card> cartOpt = cardService.findActiveCardByUserId(user.getId());
                if (cartOpt.isEmpty()) {
                    userService.setCardInputState(user, UserCardInputState.ANSWER);
                    cardService.setActivity(card, CardActivity.ACTIVE);
                    int wordsNumber = cardService.getCardByRecallMode(user.getId(), RecallMode.RECALL).size();
                    menuMessageCreator.createNotificationToUser(user.getChatId(), card.getCardMeaning(), wordsNumber, card.getInterval());
                }
            }
        }
    }
}
