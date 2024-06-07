package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yuri.petukhov.reminder.business.dto.ErrorsReportDTO;
import yuri.petukhov.reminder.business.model.MatchResult;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    @Query("SELECT mr.interval, COUNT(mr) " +
           "FROM results mr " +
           "JOIN cards c ON mr.cardId = c.id " +
           "WHERE mr.result = false AND c.user.id = :userId " +
           "GROUP BY mr.interval " +
           "ORDER BY mr.interval")
    List<Object[]> findErrorsGroupedByInterval(Long userId);

}