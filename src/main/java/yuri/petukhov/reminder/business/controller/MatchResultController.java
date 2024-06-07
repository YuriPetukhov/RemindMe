package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.service.MatchResultService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@Tag(name = "MONITORING")
public class MatchResultController {
    private final MatchResultService matchResultService;

    @GetMapping("/{userId}/report")
    @Operation(summary = "Получить отчет по ошибкам пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardsErrorsAndIntervalsReport(
            @PathVariable Long userId) {
        return ResponseEntity.ok().body(matchResultService.getCardsErrorsAndIntervalsReport(userId));
    }
}
