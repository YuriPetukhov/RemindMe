package yuri.petukhov.reminder.business.service;

public interface WordValidationService {
    boolean isMatch(String cardName, String messageText);
}
