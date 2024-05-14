package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.DTO.CardUpdate;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.service.CardService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
@Tag(name = "CARDS")
public class CardController {

    private final CardService cardService;

    @PostMapping("/{userId}")
    @Operation(summary = "Добавление карточки")
    public ResponseEntity<Void> createCard(
            @PathVariable Long userId,
            @RequestBody CardUpdate card) {
        cardService.addNewCard(card, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}/all")
    @Operation(summary = "Получить все карточки пользователя: указать количество и размер страницы")
    public ResponseEntity<List<Card>> getAllCards(
            @PathVariable Long userId,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        return ResponseEntity.ok().body(cardService.getAllCardsByUserId(userId, pageNumber, pageSize));
    }

    @GetMapping("/{userId}/{interval}")
    @Operation(summary = "Получить все карточки пользователя с определенным интервалом: указать количество и размер страницы")
    public ResponseEntity<List<Card>> getAllCardsForInterval(
            @PathVariable Long userId,
            @PathVariable ReminderInterval interval,
            @RequestParam(value = "page") Integer pageNumber,
            @RequestParam(value = "size") Integer pageSize) {
        return ResponseEntity.ok().body(cardService.getAllCardsByUserIdAndReminderInterval(userId, interval, pageNumber, pageSize));
    }
    @GetMapping("/{userId}/card-name")
    @Operation(summary = "Получить карточку пользователя по названию")
    public ResponseEntity<List<Card>> getCardByName(
            @PathVariable Long userId,
            @RequestBody String cardName) {
        return ResponseEntity.ok().body(cardService.getCardByName(userId, cardName));
    }
    @GetMapping("/{userId}/card-activity")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом активности")
    public ResponseEntity<List<Card>> getCardByCardActivity(
            @PathVariable Long userId,
            @RequestParam CardActivity activity) {
        return ResponseEntity.ok().body(cardService.getCardByCardActivity(userId, activity));
    }
    @GetMapping("/{userId}/card-recall")
    @Operation(summary = "Получить карточки пользователя с выбранным режимом напоминания")
    public ResponseEntity<List<Card>> getCardByRecallMode(
            @PathVariable Long userId,
            @RequestParam RecallMode mode) {
        return ResponseEntity.ok().body(cardService.getCardByRecallMode(userId, mode));
    }
    @GetMapping("/{userId}/card-interval")
    @Operation(summary = "Получить карточки пользователя в выбранном интервале следующего активирования")
    public ResponseEntity<List<Card>> getCardByReminderDateTime(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
        return ResponseEntity.ok().body(cardService.getCardByReminderDateTime(userId, startTime, endTime));
    }
    @PutMapping(value = "/{userId}/{cardId}")
    @Operation(summary = "Обновление карточки пользователя")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestBody CardUpdate updatedCard) {
        return ResponseEntity.ok(cardService.updateCard(userId, cardId, updatedCard));
    }

    @DeleteMapping(value = "/{userId}/{cardId}")
    @Operation(summary = "Удаление карточки")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return cardService.deleteCardById(userId, cardId)?
        ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
        ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
