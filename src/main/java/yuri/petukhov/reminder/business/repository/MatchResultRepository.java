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
}