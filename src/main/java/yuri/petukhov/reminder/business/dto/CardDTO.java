package yuri.petukhov.reminder.business.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime reminderDateTime;
}
