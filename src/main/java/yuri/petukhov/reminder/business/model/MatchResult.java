package yuri.petukhov.reminder.business.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name = "results")
@Data
@NoArgsConstructor
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cardId;
    @Enumerated(EnumType.STRING)
    @Column(name = "interval")
    private ReminderInterval interval;
    private boolean result;
    private LocalDateTime timestamp;

    public MatchResult(Long cardId, ReminderInterval interval, boolean result) {
        this.cardId = cardId;
        this.interval = interval;
        this.result = result;
        this.timestamp = LocalDateTime.now().withSecond(0).withNano(0);
    }
}
