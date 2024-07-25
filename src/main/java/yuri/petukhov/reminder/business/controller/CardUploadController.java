package yuri.petukhov.reminder.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuri.petukhov.reminder.business.service.CardUploadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardUploadController {

    private final CardUploadService cardUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCards(@RequestParam("file") MultipartFile file, Authentication authentication) {
        cardUploadService.addUploadCards(file, Long.valueOf(authentication.getName()));
        return ResponseEntity.ok("Файл успешно загружен и обработан");
    }
}
