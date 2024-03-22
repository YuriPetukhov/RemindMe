package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.model.Card;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.activity = 'ACTIVE'")
    Optional<Card> findActiveCardByUserId(Long userId);

    List<Card> findAllByReminderDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM cards c " +
           "WHERE c.recallMode = 'RECALL' AND c.id IN " +
           "(SELECT MIN(c2.id) FROM cards c2 WHERE c2.recallMode = 'RECALL' GROUP BY c2.user.id)")
    List<Card> findDistinctRecallCardsByUser();
    Optional<Card> findFirstByUserIdAndRecallMode(Long userId, RecallMode recallMode);
}

