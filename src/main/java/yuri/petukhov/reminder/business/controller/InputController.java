package yuri.petukhov.reminder.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import yuri.petukhov.reminder.business.service.InputService;
import yuri.petukhov.reminder.handling.creator.MenuMessageCreator;

@RestController
@RequestMapping("/input")
@RequiredArgsConstructor
//@PreAuthorize(value = "@cardServiceImpl.isAuthorCard(authentication.getName(), #cardId)")
@Slf4j
public class InputController {

    private final InputService inputService;
    private final MenuMessageCreator menuMessageCreator;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @Operation(summary = "Ответ пользователя через веб-интерфейс")
    public ResponseEntity<Void> response(
            @RequestBody String response,
            Authentication authentication) {
        response = response.replace("\"", "").trim();
        log.info("Answer is " + response);
        inputService.response(response, Long.valueOf(authentication.getName()));

        String userId = authentication.getName();
        messagingTemplate.convertAndSendToUser(userId, "/queue/recall", response);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/latestMessage")
    public ResponseEntity<String> getLatestMessage(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        log.info("Fetching latest message for user ID: " + userId);

        String latestMessage = menuMessageCreator.getLatestMessage(userId);
        if (latestMessage != null) {
            log.info("Latest message found: " + latestMessage);
            return ResponseEntity.ok(latestMessage);
        } else {
            log.warn("No latest message found for user ID: " + userId);
            return ResponseEntity.noContent().build();
        }
    }

}

