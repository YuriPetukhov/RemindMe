package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.dto.UnRecallWordDTO;
import yuri.petukhov.reminder.business.enums.RecallMode;
import yuri.petukhov.reminder.business.enums.ReminderInterval;
import yuri.petukhov.reminder.business.model.MatchResult;

import java.util.List;
import java.util.Objects;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    @Query("SELECT mr.interval, COUNT(mr) " +
           "FROM results mr " +
           "JOIN cards c ON mr.cardId = c.id " +
           "WHERE mr.result = false AND c.user.id = :userId " +
           "GROUP BY mr.interval " +
           "ORDER BY mr.interval")
    List<Object[]> findErrorsGroupedByInterval(Long userId);

    @Query("SELECT mr.interval, COUNT(mr) " +
           "FROM results mr " +
           "JOIN cards c ON mr.cardId = c.id " +
           "WHERE mr.result = false AND c.id = :cardId " +
           "GROUP BY mr.interval " +
           "ORDER BY mr.interval")
    List<Object[]> findErrorsByCardGroupedByInterval(Long cardId);

    @Query("SELECT c.cardMeaning, COUNT(mr) " +
           "FROM results mr JOIN cards c ON mr.cardId = c.id " +
           "WHERE mr.interval = :interval " +
           "AND mr.result = false " +
           "AND c.user.id = :userId " +
           "GROUP BY c.cardMeaning " +
           "ORDER BY COUNT(mr) DESC")
    List<Object[]> findWordsMeaningsForInterval(Long userId, ReminderInterval interval);
    @Query(value = "SELECT COUNT(*) FROM ( " +
                   "  SELECT result, " +
                   "         ROW_NUMBER() OVER (ORDER BY timestamp DESC) AS rn, " +
                   "         SUM(CASE WHEN result THEN 1 ELSE 0 END) OVER (ORDER BY timestamp DESC) AS sum_true " +
                   "  FROM results " +
                   "  WHERE cardId = :cardId AND interval = :interval " +
                   ") subquery " +
                   "WHERE sum_true = 0", nativeQuery = true)
    int countLastFalseAnswers(Long cardId, ReminderInterval interval);

    @Query(value = "WITH LastIncorrectNextInterval AS (" +
                   "  SELECT MAX(timestamp) AS last_incorrect_timestamp " +
                   "  FROM results " +
                   "  WHERE cardId = :cardId " +
                   "    AND interval = :interval + 1 " +
                   "    AND result = FALSE " +
                   "), FilteredCurrentInterval AS (" +
                   "  SELECT r.* " +
                   "  FROM results r, LastIncorrectNextInterval l " +
                   "  WHERE r.cardId = :cardId " +
                   "    AND r.interval = :interval " +
                   "    AND r.result = TRUE " +
                   "    AND r.timestamp > l.last_incorrect_timestamp " +
                   ") " +
                   "SELECT COUNT(*) " +
                   "FROM FilteredCurrentInterval", nativeQuery = true)
    int countRemainingRecall(Long cardId, ReminderInterval interval);
}