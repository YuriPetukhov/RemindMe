package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUserId(Long userId);
}
