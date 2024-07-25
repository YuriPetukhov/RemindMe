package yuri.petukhov.reminder.business.service;

import org.springframework.web.multipart.MultipartFile;

public interface CardUploadService {
    void addUploadCards(MultipartFile file, Long userId);
}
