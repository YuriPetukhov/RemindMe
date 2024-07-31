package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuri.petukhov.reminder.business.dto.CardUpdate;
import yuri.petukhov.reminder.business.service.InputService;

@RestController
@RequestMapping("/input")
@RequiredArgsConstructor
@Slf4j
public class InputController {

    private final InputService inputService;

    @PostMapping
    @Operation(summary = "Ответ пользователя через веб-интерфейс")
    public ResponseEntity<Void> response(
            @RequestBody String response,
            Authentication authentication) {
        inputService.response(response, Long.valueOf(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
