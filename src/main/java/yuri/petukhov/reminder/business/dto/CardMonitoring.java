package yuri.petukhov.reminder.business.dto;

import lombok.Data;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

@Data
public class CardMonitoring {
    private Long cardId;
    private ReminderInterval interval;
    boolean result;
}
