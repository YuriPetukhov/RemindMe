package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.CardRecordDTO;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.service.MatchResultService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@Tag(name = "MONITORING")
@PreAuthorize(value = "hasRole('ADMIN') or @userServiceImpl.isAuthorized(authentication.getName(), #userId)")
public class MatchResultController {
    private final MatchResultService matchResultService;

    @GetMapping("/{userId}/report")
    @Operation(summary = "Получить отчет по ошибкам пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardsErrorsAndIntervalsReport(
            @PathVariable Long userId) {
        return ResponseEntity.ok().body(matchResultService.getCardsErrorsAndIntervalsReport(userId));
    }

    @GetMapping("/{userId}/report/{cardId}")
    @Operation(summary = "Получить отчет по выбранной карточке пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardErrorsAndIntervalsReport(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return ResponseEntity.ok().body(matchResultService.getCardErrorsAndIntervalsReport(userId, cardId));
    }
    @GetMapping("/{userId}/records/{cardId}")
    @Operation(summary = "Получить отчет по ответам для выбранной карточки пользователя")
    public ResponseEntity<List<CardRecordDTO>> getCardRecord(
            @PathVariable Long userId,
            @PathVariable Long cardId) {
        return ResponseEntity.ok().body(matchResultService.getCardRecord(cardId));
    }
    @GetMapping("/{userId}/report/interval")
    @Operation(summary = "Получить список значений слов, у которых были ошибки в заданном интервале напоминания")
    public ResponseEntity<List<UnRecallWordDTO>> getWordsMeaningsForInterval(
            @PathVariable Long userId,
            @RequestParam ReminderInterval interval) {
        return ResponseEntity.ok().body(matchResultService.getWordsMeaningsForInterval(userId, interval));
    }
}
