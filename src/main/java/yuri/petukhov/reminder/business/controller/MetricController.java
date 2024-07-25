package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.model.Metric;
import yuri.petukhov.reminder.business.service.MetricService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
@Tag(name = "METRICS")
@Slf4j
public class MetricController {

    private final MetricService metricService;

    @GetMapping("/new-user")
    @Operation(summary = "Получить сообщения о новых пользователях")
    public ResponseEntity<List<Metric>> getNewUserMetrics() {
        return ResponseEntity.ok().body(metricService.getNewUserMetrics());
    }

    @DeleteMapping("/new-user/{messageId}")
    @Operation(summary = "Удалить сообщения о новых пользователях")
    public ResponseEntity<List<Metric>> deleteNewUserMetrics(@PathVariable Long messageId) {
        metricService.deleteNewUserMetrics(messageId);
        return ResponseEntity.noContent().build();
    }
}
