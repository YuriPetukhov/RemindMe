package yuri.petukhov.reminder.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Card> findAllByReminderDateTimeBeforeAndActivityNotAndRecallMode(
            LocalDateTime dateTime, CardActivity activity, RecallMode recallMode);

    @Query("SELECT c FROM cards c " +
            "WHERE c.recallMode = 'RECALL' " +
            "AND c.user.cardState <> 'ANSWER' " +
            "AND c.id IN (SELECT MIN(c2.id) " +
            "FROM cards c2 " +
            "WHERE c2.recallMode = 'RECALL' " +
            "GROUP BY c2.user.id)")
    List<Card> findDistinctRecallCardsByUserExcludingAnswer();

    Optional<Card> findFirstByUserIdAndRecallMode(Long userId, RecallMode recallMode);

    @Query("SELECT c FROM cards c WHERE c.user.id = :userId AND c.recallMode = 'RECALL' ORDER BY c.interval ASC")
    List<Card> findRecallCardWithSmallestInterval(@Param("userId") Long userId, Pageable pageable);

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

    @Query("SELECT c FROM cards c " +
            "WHERE c.user.id = :userId " +
            "AND c.cardName IN (" +
            "    SELECT c2.cardName FROM cards c2 " +
            "    WHERE c2.user.id = :userId " +
            "    GROUP BY c2.cardName " +
            "    HAVING COUNT(c2.cardName) > 1" +
            ")")
    List<Card> findCardNameDuplicates(Long userId);

    @Query("SELECT c FROM cards c " +
            "WHERE c.user.id = :userId " +
            "AND c.cardMeaning IN (" +
            "    SELECT c2.cardMeaning FROM cards c2 " +
            "    WHERE c2.user.id = :userId " +
            "    GROUP BY c2.cardMeaning " +
            "    HAVING COUNT(c2.cardMeaning) > 1" +
            ")")
    List<Card> findCardMeaningDuplicates(Long userId);

    @Query("SELECT COUNT(c) FROM cards c WHERE c.user.id = :userId AND c.interval = :interval AND c.activity != 'FINISHED'")
    Integer findAllCardsNumberByUserIdAndReminderInterval(Long userId, ReminderInterval interval);

    @Query("SELECT COUNT(c) FROM cards c WHERE c.interval = :interval")
    int findAllCardsNumberByReminderInterval(ReminderInterval interval);

    @Query("SELECT c FROM cards c WHERE c.user.id = :userId")
    List<Card> findUserCards(Long userId);

    @Query("SELECT c FROM cards c WHERE c.user.id = :userId ORDER BY RANDOM() LIMIT :limit")
    List<Card> findRandomCardsByUserId(Long userId, int limit);

    @Query("SELECT COUNT(c) FROM cards c WHERE c.user.id = :userId AND c.activity != 'FINISHED'")
    int getAllCardsNumber(Long userId);

    @Query("SELECT COUNT(c) FROM cards c WHERE c.activity != 'FINISHED'")
    int getAllCardsNumber();

    @Query("SELECT COUNT(c) FROM cards c WHERE c.user.id = :userId AND c.activity = :activity")
    int getFinishedCardsNumber(Long userId, CardActivity activity);

    @Query("SELECT COUNT(c) FROM cards c WHERE c.activity = :activity")
    int getFinishedCardsNumber(CardActivity activity);

}