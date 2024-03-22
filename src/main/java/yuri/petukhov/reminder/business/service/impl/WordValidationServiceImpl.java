package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.service.WordValidationService;
@Service
@RequiredArgsConstructor
@Slf4j
public class WordValidationServiceImpl implements WordValidationService {
    @Override
    public boolean isMatch(String cardName, String messageText) {
        return cardName.equalsIgnoreCase(messageText);
    }
}
