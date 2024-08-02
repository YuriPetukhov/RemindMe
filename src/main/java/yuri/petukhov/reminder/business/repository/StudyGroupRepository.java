package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.dto.CreateGroupDTO;
import yuri.petukhov.reminder.business.model.StudyGroup;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    List<StudyGroup> findAllByTeacherId(Long userId);
}
