package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.MatchResult;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
}