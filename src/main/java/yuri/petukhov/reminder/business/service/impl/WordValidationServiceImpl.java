package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.service.WordValidationService;

/**
 * Service implementation for validating words.
 * This class provides methods to check if a given word matches a specified criterion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WordValidationServiceImpl implements WordValidationService {
    /**
     * Checks if the provided card name matches the message text, ignoring case considerations.
     * @param cardName The name of the card to match.
     * @param messageText The text message to compare with the card name.
     * @return True if the card name and message text match, false otherwise.
     */
    @Override
    public boolean isMatch(String cardName, String messageText) {
        return cardName.equalsIgnoreCase(messageText);
    }
}
