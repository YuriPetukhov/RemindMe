package yuri.petukhov.reminder.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import yuri.petukhov.reminder.business.enums.CardActivity;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
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

    Page<Card> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.interval = :interval")
    Page<Card> findAllByUserIdAndReminderInterval(Long userId, ReminderInterval interval, Pageable pageable);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.cardName = :cardName")
    List<Card> findAllByCardNameAndUserId(Long userId, String cardName);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.id = :cardId")
    Optional<Card> findByIdAndUserId(Long cardId, Long userId);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.id = :cardId")
    Optional<Card> findCardByUserId(Long userId, Long cardId);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.activity = :activity")
    List<Card> findCardByCardActivity(Long userId, CardActivity activity);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.recallMode = :mode")
    List<Card> findCardByRecallMode(Long userId, RecallMode mode);
    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.reminderDateTime BETWEEN :startTime AND :endTime")
    List<Card> findCardByReminderDateTime(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}

