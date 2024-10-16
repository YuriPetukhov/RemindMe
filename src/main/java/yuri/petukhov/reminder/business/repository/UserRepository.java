package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") RoleName roleName);



}
