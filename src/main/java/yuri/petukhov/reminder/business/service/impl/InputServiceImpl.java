package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CardMonitoring;
import yuri.petukhov.reminder.business.enums.*;
import yuri.petukhov.reminder.business.exception.CardNotFoundException;
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

/**
 * Service implementation for handling user input related to card management.
 * This class provides methods to process user commands and manage card input states.
 */
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

    /**
     * Initiates the process for a user to input a new word for card creation.
     *
     * @param commandEntity The command entity containing the chat ID and user information.
     */

    @Override
    public void createInputWordMessage(CommandEntity commandEntity) {
        Long chatId = commandEntity.getChatId();
        User user = userService.findUserByChatId(commandEntity.getChatId()).orElseThrow();
        Optional<Card> cardOpt = cardService.findActiveCardByUserId(commandEntity.getUserId());
        if (cardOpt.isEmpty()) {
            userService.setCardInputState(user, UserCardInputState.WORD);
            menuMessageCreator.createInputWordMessage(chatId);
        } else {
            menuMessageCreator.createNoNewWordsMessage(chatId);
        }
    }

    /**
     * Processes a user's message, validates it against the active card, and updates card monitoring.
     *
     * @param commandEntity The command entity containing the message and user information.
     * @return CardMonitoring object containing the result of the validation and updated interval.
     */
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
            menuMessageCreator.createOkMessage(commandEntity.getChatId(), commandEntity.getUserId());
        } else {
            menuMessageCreator.createNoMessage(commandEntity.getChatId(), card.getCardName(), commandEntity.getUserId());
        }
        recallService.recallWordsForUser(commandEntity.getUserId());
        return cardMonitoring;
    }

    /**
     * Sends the main menu message to the user.
     *
     * @param commandEntity The command entity containing the chat ID.
     */

    @Override
    public void sendMenuMessage(CommandEntity commandEntity) {
        menuMessageCreator.createMenuMessage(commandEntity.getChatId());
    }

    /**
     * Sends a link to the web interface to the user.
     *
     * @param commandEntity The command entity containing the chat ID, user ID, and user role.
     */

    @Override
    public void sendWebInterfaceLink(CommandEntity commandEntity) {
        menuMessageCreator.createLinkMessage(commandEntity.getChatId(), commandEntity.getUserId(), commandEntity.getRoles());
    }

    @Override
    public void response(String response, Long userId) {
        User user = userService.findUserById(userId);
        Long chatId = user.getChatId();
        CommandEntity commandEntity = new CommandEntity();
        commandEntity.setCardState(user.getCardState());
        commandEntity.setRoles(userService.getUserRoles(chatId));
        commandEntity.setChatId(chatId);
        commandEntity.setUserId(userId);
        commandEntity.setMessageText(response);
        processMessage(commandEntity);
    }

    @Override
    public void promptGroupJoinCode(CommandEntity commandEntity) {
        Long chatId = commandEntity.getChatId();
        User user = userService.findUserByChatId(commandEntity.getChatId()).orElseThrow();
        userService.setCardInputState(user, UserCardInputState.STUDENT);
        menuMessageCreator.createGroupJoinCodeMessage(chatId);
    }

    @Override
    public void createTeacherRoleAddedMessage(CommandEntity commandEntity) {
        Long userId = commandEntity.getUserId();
        userService.addRole(userId, RoleName.ROLE_TEACHER);
        menuMessageCreator.createTeacherRoleAddedMessage(userId);
    }


    private void changeCardAndUserParameters(Card card) {
        log.info("changeCardAndUserParameters() is started");
        CardActivity activity = CardActivity.INACTIVE;
        if (card.getActivity().equals(CardActivity.FINISHED)) {
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
            newTime = time.truncatedTo(ChronoUnit.SECONDS).plusSeconds(reminderInterval.getSeconds());
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
            log.info("No active card detected");
            throw new CardNotFoundException("No active card detected");
        }
        return cardOpt.get();
    }

    /**
     * Adds a name to a new card and updates the user's card input state to MEANING.
     *
     * @param commandEntity The command entity containing the message text and user information.
     */

    @Override
    @Transactional
    public void addNameToNewCard(CommandEntity commandEntity) {
        log.info("addNameToNewCard() is started");
        String word = commandEntity.getMessageText();
        User user = userService.findUserByChatId(commandEntity.getChatId()).orElseThrow();
        cardService.createNewCard(word, user);
        userService.setCardInputState(user, UserCardInputState.MEANING);
        menuMessageCreator.createInputMeaningMessage(commandEntity);
    }

    /**
     * Adds a meaning to a new card, sets the card's activity to INACTIVE, and updates the user's card input state to NONE.
     *
     * @param commandEntity The command entity containing the message text and user information.
     */

    @Override
    @Transactional
    public void addMeaningToNewCard(CommandEntity commandEntity) {
        log.info("addMeaningToNewCard() is started");
        Card card = findActiveCard(commandEntity.getUserId());
        cardService.setActivity(card, CardActivity.INACTIVE);
        cardService.addMeaningToNewCard(card, commandEntity.getMessageText());
        User user = userService.findUserByChatId(commandEntity.getChatId()).orElseThrow();
        userService.setCardInputState(user, UserCardInputState.NONE);
        menuMessageCreator.createCardSavedMessage(commandEntity);
    }

}

