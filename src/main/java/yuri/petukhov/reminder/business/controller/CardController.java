package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardDTO;
import yuri.petukhov.reminder.business.dto.CardUpdate;
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
@Slf4j
public class CardController {

    private final CardService cardService;

    /**
     * Creates a new card for a user.
     *
     * @param card The data for creating a card.
     * @return Status CREATED if the card is successfully created.
     */
    @PostMapping
    @Operation(summary = "Добавление карточки")
    public ResponseEntity<Void> createCard(
            @RequestBody CardUpdate card,
            Authentication authentication) {
        cardService.addNewCard(card, Long.valueOf(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves all cards for a user with pagination.
     *
     * @param pageNumber The page number to retrieve.
     * @param pageSize   The number of items per page.
     * @return A list of CardDTO objects.
     */

    @GetMapping("/all")
    @Operation(summary = "Получить карточки пользователя: указать id пользователя (только для админа), количество и размер страницы")
    public ResponseEntity<List<CardDTO>> getAllCards(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize,
            Authentication authentication) {

        Long currentUserId = Long.valueOf(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin || userId == null) {
            userId = currentUserId;
        }

        return ResponseEntity.ok().body(cardService.getAllCardsByUserId(userId, pageNumber, pageSize));
    }


    /**
     * Retrieves all cards for a user within a specific reminder interval with pagination.
     *
     * @param interval   The reminder interval.
     * @param pageNumber The page number to retrieve.
     * @param pageSize   The number of items per page.
     * @return A list of CardDTO objects.
     */

    @GetMapping("/for/{interval}")
    @Operation(summary = "Получить все карточки пользователя с определенным интервалом: указать количество и размер страницы")
    public ResponseEntity<List<CardDTO>> getAllCardsForInterval(
            @PathVariable ReminderInterval interval,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getAllCardsByUserIdAndReminderInterval(Long.valueOf(authentication.getName()), interval, pageNumber, pageSize));
    }

    /**
     * Gets the number of cards for a user within a specific reminder interval.
     *
     * @param interval The reminder interval.
     * @return The number of cards for the specified interval.
     */
    @GetMapping("/{interval}/number")
    @Operation(summary = "Получить количество карточек пользователя с определенным интервалом")
    public ResponseEntity<Integer> getAllCardsNumberForInterval(
            @PathVariable ReminderInterval interval,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getAllCardsNumberByUserIdAndReminderInterval(Long.valueOf(authentication.getName()), interval));
    }

    /**
     * Retrieves a user's card by its name.
     *
     * @param cardName The name of the card.
     * @return A list of cards matching the name.
     */

    @GetMapping("/card-name")
    @Operation(summary = "Получить карточку пользователя по названию")
    public ResponseEntity<List<CardDTO>> getCardByName(
            @RequestParam String cardName,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardByName(Long.valueOf(authentication.getName()), cardName));
    }

    /**
     * Retrieves user's cards based on their activity status.
     *
     * @param activity The activity status of the cards.
     * @return A list of cards with the selected activity status.
     */

    @GetMapping("/card-activity")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом активности")
    public ResponseEntity<List<Card>> getCardByCardActivity(
            @RequestParam CardActivity activity,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardByCardActivity(Long.valueOf(authentication.getName()), activity));
    }

    /**
     * Retrieves user's cards based on their recall mode.
     *
     * @param mode The recall mode of the cards.
     * @return A list of cards with the selected recall mode.
     */

    @GetMapping("/card-recall")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом напоминания")
    public ResponseEntity<List<Card>> getCardByRecallMode(
            @RequestParam RecallMode mode,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardByRecallMode(Long.valueOf(authentication.getName()), mode));
    }

    /**
     * Retrieves user's cards within a selected range of next activation interval.
     *
     * @param startTime The start time of the interval.
     * @param endTime   The end time of the interval.
     * @return A list of cards within the specified interval.
     */

    @GetMapping("/card-interval")
    @Operation(summary = "Получить карточки пользователя в выбранном интервале следующего активирования")
    public ResponseEntity<List<Card>> getCardByReminderDateTime(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardByReminderDateTime(Long.valueOf(authentication.getName()), startTime, endTime));
    }

    /**
     * Retrieves duplicates in card names or answers for a user.
     *
     * @return A list of cards with duplicate names or answers.
     */

    @GetMapping("/names-duplicates")
    @Operation(summary = "Получить дубликаты слов или ответов карточек пользователя")
    public ResponseEntity<List<Card>> getCardNameDuplicates(
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardNameDuplicates(Long.valueOf(authentication.getName())));
    }

    /**
     * Retrieves duplicates in card meanings or questions for a user.
     *
     * @return A list of cards with duplicate meanings or questions.
     */

    @GetMapping("/meanings-duplicates")
    @Operation(summary = "Получить дубликаты значений или вопросов карточек пользователя")
    public ResponseEntity<List<Card>> getCardMeaningDuplicates(
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getCardMeaningDuplicates(Long.valueOf(authentication.getName())));
    }

    /**
     * Retrieves a user's card by its ID.
     *
     * @param cardId The ID of the card.
     * @return The card with the specified ID.
     */

    @GetMapping("/{cardId}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
    @Operation(summary = "Получить карточку пользователя по id")
    public ResponseEntity<CardDTO> getCardDTO(
            @PathVariable Long cardId,
            Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getUserCardById(cardId));
    }

    /**
     * Updates a user's card.
     *
     * @param cardId      The ID of the card to update.
     * @param updatedCard The new data for the card.
     * @return The updated card.
     */

    @PutMapping(value = "/{cardId}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
    @Operation(summary = "Обновление карточки пользователя")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long cardId,
            @RequestBody CardUpdate updatedCard,
            Authentication authentication) {
        return ResponseEntity.ok(cardService.updateCard(Long.valueOf(authentication.getName()), cardId, updatedCard));
    }

    /**
     * Deletes a user's card by its ID.
     *
     * @param cardId The ID of the card to delete.
     */

    @DeleteMapping(value = "/{cardId}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or @cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
    @Operation(summary = "Удаление карточки")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            Authentication authentication) {
        cardService.deleteCardById(Long.valueOf(authentication.getName()), cardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a set of 60 random cards for a user.
     *
     * @return A list of 60 random CardDTO objects.
     */

    @GetMapping("/random-set")
    @Operation(summary = "Получение набора 60 случайных карточек")
    public ResponseEntity<List<CardDTO>> getCards(Authentication authentication) {
        List<CardDTO> cards = cardService.getAllCardsDTOByUserId(Long.valueOf(authentication.getName()));
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves a random card for a user.
     *
     * @return A random CardDTO object.
     */

    @GetMapping("/random-card")
    @Operation(summary = "Получение случайной карточки")
    public ResponseEntity<CardDTO> getRandomCard(Authentication authentication) {
        log.info("user " + authentication.getName());
        return ResponseEntity.ok(cardService.getRandomCardsDTOByUserId(Long.valueOf(authentication.getName())));
    }

    /**
     * Retrieves statistics for all intervals of reminders.
     *
     * @return A list of integers representing statistics for all intervals.
     */
    @GetMapping("/intervals/stats")
    @Operation(summary = "Получить статистику карточек пользователя для всех интервалов")
    public ResponseEntity<List<Integer>> getStatsForAllIntervals(Authentication authentication) {
        return ResponseEntity.ok().body(cardService.getStatsForAllIntervals(Long.valueOf(authentication.getName())));
    }

}
