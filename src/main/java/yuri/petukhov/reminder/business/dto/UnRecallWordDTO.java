package yuri.petukhov.reminder.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnRecallWordDTO {
    private String meaning;
    private int failCount;
}
