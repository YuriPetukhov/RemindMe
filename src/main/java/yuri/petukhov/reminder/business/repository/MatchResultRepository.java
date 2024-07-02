package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.MatchResult;

import java.util.List;
import java.util.Objects;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

//    @Query("SELECT mr.interval, COUNT(mr) " +
//           "FROM results mr " +
//           "JOIN cards c ON mr.cardId = c.id " +
//           "WHERE mr.result = false AND c.user.id = :userId " +
//           "GROUP BY mr.interval " +
//           "ORDER BY mr.interval")
//    List<Object[]> findErrorsGroupedByInterval(Long userId);
//
//    @Query("SELECT mr.interval, COUNT(mr) " +
//           "FROM results mr " +
//           "JOIN cards c ON mr.cardId = c.id " +
//           "WHERE mr.result = false AND c.id = :cardId " +
//           "GROUP BY mr.interval " +
//           "ORDER BY mr.interval")
//    List<Object[]> findErrorsByCardGroupedByInterval(Long cardId);

    @Query("SELECT mr.interval, COUNT(mr), SUM(CASE WHEN mr.result = false THEN 1 ELSE 0 END) " +
           "FROM results mr " +
           "JOIN cards c ON mr.cardId = c.id " +
           "WHERE c.user.id = :userId " +
           "GROUP BY mr.interval " +
           "ORDER BY mr.interval")
    List<Object[]> findAttemptsAndErrorsGroupedByInterval(Long userId);

    @Query("SELECT mr.interval, COUNT(mr), SUM(CASE WHEN mr.result = false THEN 1 ELSE 0 END) " +
           "FROM results mr " +
           "JOIN cards c ON mr.cardId = c.id " +
           "WHERE c.id = :cardId " +
           "GROUP BY mr.interval " +
           "ORDER BY mr.interval")
    List<Object[]> findAttemptsAndErrorsByCardGroupedByInterval(Long cardId);

    @Query("SELECT c.cardMeaning, COUNT(mr) " +
           "FROM results mr JOIN cards c ON mr.cardId = c.id " +
           "WHERE mr.interval = :interval " +
           "AND mr.result = false " +
           "AND c.user.id = :userId " +
           "GROUP BY c.cardMeaning " +
           "ORDER BY COUNT(mr) DESC")
    List<Object[]> findWordsMeaningsForInterval(Long userId, ReminderInterval interval);
    @Query("SELECT COUNT(mr) " +
           "FROM results mr " +
           "WHERE mr.cardId = :cardId " +
           "  AND mr.interval = :interval " +
           "  AND mr.result = false " +
           "  AND mr.timestamp > COALESCE(( " +
           "    SELECT MAX(mr2.timestamp) " +
           "    FROM results mr2 " +
           "    WHERE mr2.cardId = mr.cardId " +
           "      AND mr2.interval = mr.interval " +
           "      AND mr2.result = true " +
           "), '1970-01-01')")
    int countLastFalseAnswers(Long cardId, ReminderInterval interval);



    @Query("SELECT COUNT(mr) " +
           "FROM results mr " +
           "WHERE mr.cardId = :cardId " +
           "  AND mr.interval = :interval " +
           "  AND mr.result = true " +
           "  AND mr.timestamp >= COALESCE(( " +
           "    SELECT MAX(mr2.timestamp) " +
           "    FROM results mr2 " +
           "    WHERE mr2.cardId = :cardId " +
           "      AND mr2.interval = :nextInterval " +
           "), '1970-01-01')")
    int countRemainingRecall(Long cardId, ReminderInterval interval, ReminderInterval nextInterval);
    @Query(value = "SELECT timestamp, interval, result FROM results " +
                   "WHERE card_id = :cardId " +
                   "ORDER BY timestamp ASC", nativeQuery = true)
    List<Object[]> generateCardRecord(@Param("cardId") Long cardId);
}