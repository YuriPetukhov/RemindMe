package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    void updateUser(User user);
    void removeUser(Long id);

    User findUserById(Long id);

    Optional<User> findUserByChatId(Long chatId);

    List<String> getUserRoles(Long chatId, String userName);

    void setCardInputState(User user, UserCardInputState userCardInputState);

    UserCardInputState getUserCardState(Long chatId);

    Long getUserId(Long chatId);

    void createNewUser(Long chatId, String userName);

    void addRole(Long userId, RoleName role);

    List<String> getCurrentUserRoles(long l);

    void deleteRoleByUser(Long userId, RoleName roleName);
}
