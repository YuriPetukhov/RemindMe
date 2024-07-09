package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
}
