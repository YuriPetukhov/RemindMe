package yuri.petukhov.reminder.business.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "card_sets")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_name")
    private String setName;

    @Column(name = "set_description")
    private String setDescription;

    @ManyToMany
    @JoinTable(
            name = "card_set_cards",
            joinColumns = @JoinColumn(name = "card_set_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> cards;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}

