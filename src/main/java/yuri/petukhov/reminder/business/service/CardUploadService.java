package yuri.petukhov.reminder.business.service;

import org.springframework.web.multipart.MultipartFile;

public interface CardUploadService {
    void addUploadCards(MultipartFile file, String cardSetName, String setDescription, String activationStart, int cardsPerBatch, int activationInterval, String intervalUnit, Long userId);
}
