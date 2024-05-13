package yuri.petukhov.reminder.business.model;

import jakarta.persistence.*;
import lombok.*;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "cards")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word")
    private String cardName;

    @Column(name = "meaning")
    private String cardMeaning;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_activity")
    private CardActivity activity;

    @Enumerated(EnumType.STRING)
    @Column(name = "recall_mode")
    private RecallMode recallMode;

    @Column(name = "next_date_time")
    LocalDateTime reminderDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval")
    private ReminderInterval interval;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static Card createCard(String cardName, User user) {
        if (cardName == null || cardName.isBlank()) {
            return null;
        }
        Card card = new Card();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime reminderTime = currentTime.withSecond(0).withNano(0).plusMinutes(20);

        card.setCardName(cardName);
        card.setActivity(CardActivity.ACTIVE);
        card.setRecallMode(RecallMode.NONE);
        card.setReminderDateTime(reminderTime);
        card.setInterval(ReminderInterval.MINUTES_20);
        card.setUser(user);
        return card;

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{" +
               "id=" + id +
               ", cardName='" + cardName + '\'' +
               ", cardMeaning='" + cardMeaning + '\'' +
               ", activity=" + activity +
               ", reminderDateTime=" + reminderDateTime +
               ", interval=" + interval +
               '}';
    }
}
