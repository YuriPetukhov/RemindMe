package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.service.InputService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

@RestController
@RequestMapping("/input")
@RequiredArgsConstructor
@PreAuthorize(value = "@cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
@Slf4j
public class InputController {

    private final InputService inputService;
    private final MenuMessageCreator menuMessageCreator;

    @PostMapping
    @Operation(summary = "Ответ пользователя через веб-интерфейс")
    public ResponseEntity<Void> response(
            @RequestBody String response,
            Authentication authentication) {
        response = response.replace("\"", "").trim();
        log.info("Answer is " + response);
        inputService.response(response, Long.valueOf(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/latestMessage")
    public ResponseEntity<String> getLatestMessage(
            Authentication authentication) {
        String latestMessage = menuMessageCreator.getLatestMessage(Long.valueOf(authentication.getName()));
        if (latestMessage != null) {
            return ResponseEntity.ok(latestMessage);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
