package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);

}
