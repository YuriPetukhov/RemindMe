package yuri.petukhov.reminder.business.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "study_groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @ManyToMany
    @JoinTable(
            name = "study_group_users",
            joinColumns = @JoinColumn(name = "study_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> students;

    @ManyToMany
    @JoinTable(
            name = "study_group_card_sets",
            joinColumns = @JoinColumn(name = "study_group_id"),
            inverseJoinColumns = @JoinColumn(name = "card_set_id")
    )
    private List<CardSet> cardSets;
}
