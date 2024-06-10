package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CardMonitoring;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.service.*;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;
import yuri.petukhov.reminder.business.dto.CommandEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InputServiceImpl implements InputService {
    private final CardService cardService;
    private final UserService userService;
    private final MenuMessageCreator menuMessageCreator;
    private final WordValidationService wordValidationService;
    private final RecallService recallService;
    private final ReminderIntervalService reminderIntervalService;

    @Override
    public void createInputWordMessage(CommandEntity commandEntity) {
        Long chatId = commandEntity.getChatId();
        User user = userService.findUserByChatId(commandEntity.getChatId());
        userService.setCardInputState(user, UserCardInputState.WORD);
        menuMessageCreator.createInputWordMessage(chatId);
    }
    @Override
    public CardMonitoring processMessage(CommandEntity commandEntity) {
        log.info("processMessage() is started");
        Card card = findActiveCard(commandEntity.getUserId());
        boolean result = wordValidationService.isMatch(card.getCardName(), commandEntity.getMessageText());
        CardMonitoring cardMonitoring = new CardMonitoring();
        cardMonitoring.setCardId(card.getId());
        cardMonitoring.setResult(result);
        cardMonitoring.setInterval(card.getInterval());
        defineNewReminderParameter(card, result);
        changeCardAndUserParameters(card);
        if (result) {
            menuMessageCreator.createOkMessage(commandEntity.getChatId());
        } else {
            menuMessageCreator.createNoMessage(commandEntity.getChatId(), card.getCardName());
        }
        recallService.recallWordsForUser(commandEntity.getUserId());
        return cardMonitoring;
    }


    private void changeCardAndUserParameters(Card card) {
        log.info("changeCardAndUserParameters() is started");
        CardActivity activity = CardActivity.INACTIVE;
        if(card.getActivity().equals(CardActivity.FINISHED)) {
            activity = CardActivity.FINISHED;
        }
        cardService.setActivity(card, activity);
        cardService.setRecallMode(card, RecallMode.NONE);
    }
    private void defineNewReminderParameter(Card card, boolean result) {
        log.info("defineNewReminderParameter() is started");
        ReminderInterval reminderInterval = card.getInterval();
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime newTime;
        if (result && !reminderInterval.equals(ReminderInterval.DAYS_60)) {
            reminderInterval = reminderIntervalService.updateCurrentReminderInterval(card);
            newTime = time.truncatedTo(ChronoUnit.SECONDS).plusSeconds(reminderInterval.nextInterval().getSeconds());
        } else if (result) {
            log.info("closing the card");
            cardService.setActivity(card, CardActivity.FINISHED);
            menuMessageCreator.createCompletedMessage(card.getUser().getChatId());
            return;
        } else {
            newTime = time.truncatedTo(ChronoUnit.SECONDS).plusSeconds(ReminderInterval.MINUTES_20.getSeconds());
            reminderInterval = ReminderInterval.MINUTES_20;
        }

        if (newTime.toLocalTime().isBefore(LocalTime.of(0, 0)) || newTime.toLocalTime().isAfter(LocalTime.of(22, 0))) {
            newTime = newTime.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        } else if (newTime.toLocalTime().isAfter(LocalTime.of(0, 0)) && newTime.toLocalTime().isBefore(LocalTime.of(8, 0))) {
            newTime = newTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
        }

        cardService.setReminderInterval(card, newTime, reminderInterval);
    }


    private Card findActiveCard(Long userId) {
        log.info("findActiveCard() is started");
        Optional<Card> cardOpt = cardService.findActiveCardByUserId(userId);
        if (cardOpt.isEmpty()) {
            log.info("An active card was not detected");
            throw new NoSuchElementException();
        }
        return cardOpt.get();
    }

    @Override
    @Transactional
    public void addNameToNewCard(CommandEntity commandEntity) {
        log.info("addNameToNewCard() is started");
        String word = commandEntity.getMessageText();
        User user = userService.findUserByChatId(commandEntity.getChatId());
        cardService.createNewCard(word, user);
        userService.setCardInputState(user, UserCardInputState.MEANING);
        menuMessageCreator.createInputMeaningMessage(commandEntity);
    }

    @Override
    @Transactional
    public void addMeaningToNewCard(CommandEntity commandEntity) {
        log.info("addMeaningToNewCard() is started");
        Card card = findActiveCard(commandEntity.getUserId());
        cardService.setActivity(card, CardActivity.INACTIVE);
        cardService.addMeaningToNewCard(card, commandEntity.getMessageText());
        User user = userService.findUserByChatId(commandEntity.getChatId());
        userService.setCardInputState(user, UserCardInputState.NONE);
        menuMessageCreator.createCardSavedMessage(commandEntity);
    }

}

