package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.service.AdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "ADMIN")
@PreAuthorize(value = "hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users/intervals/stats")
    @Operation(summary = "Получить статистику карточек пользователя для всех интервалов")
    public ResponseEntity<List<Integer>> getStatsForAllIntervals() {
        return ResponseEntity.ok().body(adminService.getStatsForAllIntervals());
    }

    @GetMapping("/users/report")
    @Operation(summary = "Получить отчет по ошибкам пользователя с сортировкой по интервалам напоминания")
    public ResponseEntity<List<ErrorsReportDTO>> getCardsErrorsAndIntervalsReport() {
        return ResponseEntity.ok().body(adminService.getCardsErrorsAndIntervalsReport());
    }
}
