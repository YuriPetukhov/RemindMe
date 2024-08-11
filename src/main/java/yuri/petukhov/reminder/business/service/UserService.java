package yuri.petukhov.reminder.business.service;

import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.dto.CardActivateDTO;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.Card;
import yuri.petukhov.reminder.business.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);

    void updateUser(User user);

    void removeUser(Long id);

    User findUserById(Long id);

    Optional<User> findUserByChatId(Long chatId);

    List<String> getUserRoles(Long chatId, String userName);

    @Transactional
    List<String> getUserRoles(Long chatId);

    void setCardInputState(User user, UserCardInputState userCardInputState);

    UserCardInputState getUserCardState(Long chatId);

    Long getUserId(Long chatId);

    void createNewUser(Long chatId, String userName);

    void addRole(Long userId, RoleName role);

    List<String> getCurrentUserRoles(long l);

    void deleteRoleByUser(Long userId, RoleName roleName);

    void setCardSet(User user, List<Card> cards, CardActivateDTO cardActivateDTO);

    LocalDateTime getNextReminderDate(LocalDateTime currentReminderDate, int activationInterval, String intervalUnit);
}
