package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.enums.UserRole;
import yuri.petukhov.reminder.business.model.User;

import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    void updateUser(User user);
    void removeUser(Long id);

    User findUserById(Long id);

    Optional<User> findUserByChatId(Long chatId);

    UserRole getUserState(Long chatId, String userName);
    void setUserRole(Long chatId, UserRole state);

    void setCardInputState(User user, UserCardInputState userCardInputState);

    UserCardInputState getUserCardState(Long chatId);

    Long getUserId(Long chatId);
}
