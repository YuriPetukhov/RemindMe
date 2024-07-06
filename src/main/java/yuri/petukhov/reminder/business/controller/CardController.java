package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.dto.FindCardDTO;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.service.CardService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing reminder cards.
 * Allows performing CRUD operations on cards, as well as retrieving statistics and random cards.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
@Tag(name = "CARDS")
@PreAuthorize(value = "hasRole('ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class CardController {

    private final CardService cardService;

    /**
     * Creates a new card for a user.
     * @param userId The ID of the user.
     * @param card The data for creating a card.
     * @return Status CREATED if the card is successfully created.
     */
    @PostMapping("/{userId}")
    @Operation(summary = "Добавление карточки")
    public ResponseEntity<Void> createCard(
            @PathVariable Long userId,
            @RequestBody CardUpdate card) {
        cardService.addNewCard(card, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves all cards for a user with pagination.
     * @param userId The ID of the user.
     * @param pageNumber The page number to retrieve.
     * @param pageSize The number of items per page.
     * @return A list of CardDTO objects.
     */

    @GetMapping("/{userId}/all")
    @Operation(summary = "Получить все карточки пользователя: указать количество и размер страницы")
    public ResponseEntity<List<CardDTO>> getAllCards(
            @PathVariable Long userId,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        return ResponseEntity.ok().body(cardService.getAllCardsByUserId(userId, pageNumber, pageSize));
    }

    /**
     * Retrieves all cards for a user within a specific reminder interval with pagination.
     * @param userId The ID of the user.
     * @param interval The reminder interval.
     * @param pageNumber The page number to retrieve.
     * @param pageSize The number of items per page.
     * @return A list of CardDTO objects.
     */

    @GetMapping("/{userId}/for/{interval}")
    @Operation(summary = "Получить все карточки пользователя с определенным интервалом: указать количество и размер страницы")
    public ResponseEntity<List<CardDTO>> getAllCardsForInterval(
            @PathVariable Long userId,
            @PathVariable ReminderInterval interval,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        return ResponseEntity.ok().body(cardService.getAllCardsByUserIdAndReminderInterval(userId, interval, pageNumber, pageSize));
    }

    /**
     * Gets the number of cards for a user within a specific reminder interval.
     * @param userId The ID of the user.
     * @param interval The reminder interval.
     * @return The number of cards for the specified interval.
     */
    @GetMapping("/{userId}/{interval}/number")
    @Operation(summary = "Получить количество карточек пользователя с определенным интервалом")
    public ResponseEntity<Integer> getAllCardsNumberForInterval(
            @PathVariable Long userId,
            @PathVariable ReminderInterval interval) {
        return ResponseEntity.ok().body(cardService.getAllCardsNumberByUserIdAndReminderInterval(userId, interval));
    }

    /**
     * Retrieves a user's card by its name.
     * @param userId The ID of the user.
     * @param cardName The name of the card.
     * @return A list of cards matching the name.
     */

    @GetMapping("/{userId}/card-name")
    @Operation(summary = "Получить карточку пользователя по названию")
    public ResponseEntity<List<FindCardDTO>> getCardByName(
            @PathVariable Long userId,
            @RequestParam String cardName) {
        return ResponseEntity.ok().body(cardService.getCardByName(userId, cardName));
    }

    /**
     * Retrieves user's cards based on their activity status.
     * @param userId The ID of the user.
     * @param activity The activity status of the cards.
     * @return A list of cards with the selected activity status.
     */

    @GetMapping("/{userId}/card-activity")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом активности")
    public ResponseEntity<List<Card>> getCardByCardActivity(
            @PathVariable Long userId,
            @RequestParam CardActivity activity) {
        return ResponseEntity.ok().body(cardService.getCardByCardActivity(userId, activity));
    }

    /**
     * Retrieves user's cards based on their recall mode.
     * @param userId The ID of the user.
     * @param mode The recall mode of the cards.
     * @return A list of cards with the selected recall mode.
     */

    @GetMapping("/{userId}/card-recall")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом напоминания")
    public ResponseEntity<List<Card>> getCardByRecallMode(
            @PathVariable Long userId,
            @RequestParam RecallMode mode) {
        return ResponseEntity.ok().body(cardService.getCardByRecallMode(userId, mode));
    }

    /**
     * Retrieves user's cards within a selected range of next activation interval.
     * @param userId The ID of the user.
     * @param startTime The start time of the interval.
     * @param endTime The end time of the interval.
     * @return A list of cards within the specified interval.
     */

    @GetMapping("/{userId}/card-interval")
    @Operation(summary = "Получить карточки пользователя в выбранном интервале следующего активирования")
    public ResponseEntity<List<Card>> getCardByReminderDateTime(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
        return ResponseEntity.ok().body(cardService.getCardByReminderDateTime(userId, startTime, endTime));
    }

    /**
     * Retrieves duplicates in card names or answers for a user.
     * @param userId The ID of the user.
     * @return A list of cards with duplicate names or answers.
     */

    @GetMapping("/{userId}/names-duplicates")
    @Operation(summary = "Получить дубликаты слов или ответов карточек пользователя")
    public ResponseEntity<List<Card>> getCardNameDuplicates(
            @PathVariable Long userId) {
        return ResponseEntity.ok().body(cardService.getCardNameDuplicates(userId));
    }

    /**
     * Retrieves duplicates in card meanings or questions for a user.
     * @param userId The ID of the user.
     * @return A list of cards with duplicate meanings or questions.
     */

    @GetMapping("/{userId}/meanings-duplicates")
    @Operation(summary = "Получить дубликаты значений или вопросов карточек пользователя")
    public ResponseEntity<List<Card>> getCardMeaningDuplicates(
            @PathVariable Long userId) {
        return ResponseEntity.ok().body(cardService.getCardMeaningDuplicates(userId));
    }

    /**
     * Retrieves a user's card by its ID.
     * @param userId The ID of the user.
     * @param cardId The ID of the card.
     * @return The card with the specified ID.
     */

    @GetMapping("/{userId}/{cardId}")
    @Operation(summary = "Получить карточку пользователя по id")
    public ResponseEntity<Card> getUserCardById(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return ResponseEntity.ok().body(cardService.getUserCardById(userId, cardId));
    }

    /**
     * Updates a user's card.
     * @param userId The ID of the user.
     * @param cardId The ID of the card to update.
     * @param updatedCard The new data for the card.
     * @return The updated card.
     */

    @PutMapping(value = "/{userId}/{cardId}")
    @Operation(summary = "Обновление карточки пользователя")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestBody CardUpdate updatedCard) {
        return ResponseEntity.ok(cardService.updateCard(userId, cardId, updatedCard));
    }

    /**
     * Deletes a user's card by its ID.
     * @param userId The ID of the user.
     * @param cardId The ID of the card to delete.
     */

    @DeleteMapping(value = "/{userId}/{cardId}")
    @Operation(summary = "Удаление карточки")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        cardService.deleteCardById(userId, cardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a set of 60 random cards for a user.
     * @param userId The ID of the user.
     * @return A list of 60 random CardDTO objects.
     */

    @GetMapping("/{userId}/random-set")
    @Operation(summary = "Получение набора 60 случайных карточек")
    public ResponseEntity<List<CardDTO>> getCards(@PathVariable("userId") Long userId) {
        List<CardDTO> cards = cardService.getAllCardsDTOByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves a random card for a user.
     * @param userId The ID of the user.
     * @return A random CardDTO object.
     */

    @GetMapping("/{userId}/random-card")
    @Operation(summary = "Получение случайной карточки")
    public ResponseEntity<CardDTO> getRandomCard(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(cardService.getRandomCardsDTOByUserId(userId));
    }
    /**
     * Retrieves statistics for all intervals of reminders.
     * @param userId The ID of the user.
     * @return A list of integers representing statistics for all intervals.
     */
    @GetMapping("/{userId}/intervals/stats")
    @Operation(summary = "Получить статистику карточек пользователя для всех интервалов")
    public ResponseEntity<List<Integer>> getStatsForAllIntervals(@PathVariable Long userId) {
        return ResponseEntity.ok().body(cardService.getStatsForAllIntervals(userId));
    }

}
