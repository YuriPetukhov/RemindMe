package yuri.petukhov.reminder.business.exception;

public class CardSetNotFoundException extends RuntimeException {
    public CardSetNotFoundException(String message) {
        super(message);
    }
}

