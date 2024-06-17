package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.enums.UserRole;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j  // SLF4J logging
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void updateUser(User user) {
        saveUser(user);
    }

    @Override
    public void removeUser(Long id) {
        userRepository.delete(findUserById(id));
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with ID " + id + " not found.");
        }
        return optionalUser.get();
    }

    @Override
    public Optional<User> findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }
    @Override
    public UserRole getUserState(Long chatId, String userName) {
        UserRole userRole = UserRole.USER;
        Optional<User> optUser = findUserByChatId(chatId);
        if(optUser.isPresent()) {
            log.info("User state was detected");
            userRole = optUser.get().getRole();
        } else {
            createNewUser(chatId, userName);
        }
        return userRole;
    }

    @Override
    public void setUserRole(Long chatId, UserRole state) {
        User user = findUserByChatId(chatId).orElseThrow();
        user.setRole(state);
        saveUser(user);
    }

    @Override
    public void setCardInputState(User user, UserCardInputState userCardInputState) {
        user.setCardState(userCardInputState);
        saveUser(user);
    }

    @Override
    public UserCardInputState getUserCardState(Long chatId) {
        User user = findUserByChatId(chatId).orElseThrow();
        return user.getCardState();
    }

    @Override
    public Long getUserId(Long chatId) {
        User user = userRepository.findByChatId(chatId).orElseThrow();
        return user.getId();
    }

    private void createNewUser(Long chatId, String userName) {
        User user = new User();
        user.setChatId(chatId);
        user.setUserName(userName);
        user.setRole(UserRole.USER);
        user.setCardState(UserCardInputState.NONE);
        log.info("A NEW user was saved");
        saveUser(user);
    }
    public boolean isAuthorized(Long chatId, Long userId) {
        Optional<User> user = findUserByChatId(chatId);
        return user.isPresent() && user.get().getId().equals(userId);
    }
}
