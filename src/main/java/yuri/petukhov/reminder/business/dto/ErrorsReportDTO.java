package yuri.petukhov.reminder.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

@Data
@AllArgsConstructor
public class ErrorsReportDTO {
    private ReminderInterval interval;
    private Long attemptsCount;
    private Long errorCount;
}
