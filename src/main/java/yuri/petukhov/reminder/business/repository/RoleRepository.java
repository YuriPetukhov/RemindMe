package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
