package yuri.petukhov.reminder.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuri.petukhov.reminder.business.service.CardUploadService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardUploadController {

    private final CardUploadService cardUploadService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCards(
            @RequestParam(value = "file") MultipartFile file,
//            @RequestParam(value = "cardSetName", required = false) String cardSetName,
//            @RequestParam(value = "setDescription", required = false) String setDescription,
            @RequestParam(value = "activationStart", required = false) String activationStart,
            @RequestParam(value = "cardsPerBatch", required = false, defaultValue = "0") int cardsPerBatch,
            @RequestParam(value = "activationInterval", required = false, defaultValue = "0") int activationInterval,
            @RequestParam(value = "intervalUnit", required = false) String intervalUnit,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        cardUploadService.addUploadCards(file, activationStart, cardsPerBatch, activationInterval, intervalUnit, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File and data successfully uploaded and processed");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadSetCards")
    public ResponseEntity<Map<String, String>> uploadSetCards(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "cardSetId", required = true) Long cardSetId,
            Authentication authentication) {

        cardUploadService.uploadSetCards(file, cardSetId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File and data successfully uploaded and processed");

        return ResponseEntity.ok(response);
    }
}
