package yuri.petukhov.reminder.business.dto;

import lombok.Data;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

import java.time.LocalDateTime;

@Data
public class CardRecordDTO {
    private LocalDateTime timestamp;
    private ReminderInterval interval;
    private boolean result;
}
