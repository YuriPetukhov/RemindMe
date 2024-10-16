package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardRecordDTO;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.service.MatchResultService;

import java.util.List;

/**
 * Controller for monitoring user interactions with reminder cards.
 * Provides an API for retrieving error reports, response records, and data analysis based on reminder intervals.
 * Access to the methods of this controller is restricted to users with the 'ADMIN' role or those authorized by the userServiceImpl.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@Tag(name = "MONITORING")
public class MatchResultController {
    private final MatchResultService matchResultService;

    /**
     * Retrieves a report of the user's errors sorted by reminder intervals.
     * @return A list of error reports and intervals.
     */

    @GetMapping("/report")
    @Operation(summary = "Получить отчет по ошибкам пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardsErrorsAndIntervalsReport(
            Authentication authentication) {
        return ResponseEntity.ok().body(matchResultService.getCardsErrorsAndIntervalsReport(Long.valueOf(authentication.getName())));
    }

    /**
     * Retrieves a report of errors for a specific user's card sorted by reminder intervals.
     * @param cardId The ID of the card.
     * @return A list of error reports and intervals for the selected card.
     */

    @GetMapping("/report/{cardId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
    @Operation(summary = "Получить отчет по выбранной карточке пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardErrorsAndIntervalsReport(
            @PathVariable Long cardId,
            Authentication authentication) {
        return ResponseEntity.ok().body(matchResultService.getCardErrorsAndIntervalsReport(Long.valueOf(authentication.getName()), cardId));
    }

    /**
     * Retrieves records of responses for a specific user's card.
     * @param cardId The ID of the card.
     * @return A list of response records for the card.
     */
    @GetMapping("/records/{cardId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
    @Operation(summary = "Получить отчет по ответам для выбранной карточки пользователя")
    public ResponseEntity<List<CardRecordDTO>> getCardRecord(
            @PathVariable Long cardId) {
        return ResponseEntity.ok().body(matchResultService.getCardRecord(cardId));
    }

    /**
     * Retrieves a list of word meanings that had errors during a specified reminder interval.
     * @param interval The reminder interval.
     * @return A list of word meanings with errors in the specified interval.
     */
    @GetMapping("/report/interval")
    @Operation(summary = "Получить список значений слов, у которых были ошибки в заданном интервале напоминания")
    public ResponseEntity<List<UnRecallWordDTO>> getWordsMeaningsForInterval(
            @RequestParam ReminderInterval interval,
            Authentication authentication) {
        return ResponseEntity.ok().body(matchResultService.getWordsMeaningsForInterval(Long.valueOf(authentication.getName()), interval));
    }
}
