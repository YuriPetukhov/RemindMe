package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuri.petukhov.reminder.business.enums.RoleName;
import yuri.petukhov.reminder.business.enums.UserCardInputState;
import yuri.petukhov.reminder.business.exception.UserNotFoundException;
import yuri.petukhov.reminder.business.model.Role;
import yuri.petukhov.reminder.business.model.User;
import yuri.petukhov.reminder.business.repository.UserRepository;
import yuri.petukhov.reminder.business.service.AdminService;
import yuri.petukhov.reminder.business.service.RoleService;
import yuri.petukhov.reminder.business.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user-related operations.
 * This class provides methods to save, update, remove, and find users, as well as manage user roles and states.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AdminService adminService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Saves a user to the repository.
     * @param user The user to save.
     */

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Updates a user in the repository.
     * @param user The user to update.
     */

    @Override
    public void updateUser(User user) {
        saveUser(user);
    }

    /**
     * Removes a user from the repository by their ID.
     * @param id The ID of the user to remove.
     */

    @Override
    public void removeUser(Long id) {
        userRepository.delete(findUserById(id));
    }

    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return The found user.
     */
    @Override
    public User findUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        return optionalUser.get();
    }

    /**
     * Finds a user by their chat ID.
     * @param chatId The chat ID of the user to find.
     * @return An Optional containing the found user or empty if not found.
     */

    @Override
    public Optional<User> findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    /**
     * Retrieves the list of role names of a user by their chat ID.
     * If the user is found, returns a list of their role names.
     * If the user is not found, a new user is created with the USER role
     * and a list containing only the USER role name is returned.
     * @param chatId The chat ID of the user.
     * @param userName The username of the user.
     * @return The list of RoleName states of the user.
     */

    @Transactional
    @Override
    public List<String> getUserRoles(Long chatId, String userName) {
        Optional<User> optUser = findUserByChatId(chatId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            List<Role> roles = user.getRoles();
            List<String> roleNames = roles.stream()
                    .map(role -> role.getRoleName().name())
                    .collect(Collectors.toList());
            log.info("User roles were detected: " + roleNames);
            return roleNames;
        } else {
            createNewUser(chatId, userName);
            return Collections.singletonList(RoleName.ROLE_USER.name());
        }
    }

    @Transactional
    @Override
    public List<String> getUserRoles(Long chatId) {
        Optional<User> optUser = findUserByChatId(chatId);
        List<String> roleNames = null;
        if (optUser.isPresent()) {
            User user = optUser.get();
            List<Role> roles = user.getRoles();
            roleNames = roles.stream()
                    .map(role -> role.getRoleName().name())
                    .collect(Collectors.toList());
            log.info("User roles were detected: " + roleNames);
        }
        return roleNames;
    }

    /**
     * Sets the card input state of a user.
     * @param user The user to set the state for.
     * @param userCardInputState The new card input state to set.
     */

    @Override
    public void setCardInputState(User user, UserCardInputState userCardInputState) {
        user.setCardState(userCardInputState);
        saveUser(user);
    }

    /**
     * Gets the card input state of a user by their chat ID.
     * @param chatId The chat ID of the user.
     * @return The UserCardInputState of the user.
     */

    @Override
    public UserCardInputState getUserCardState(Long chatId) {
        User user = findUserByChatId(chatId).orElseThrow();
        return user.getCardState();
    }

    /**
     * Gets the user ID by their chat ID.
     * @param chatId The chat ID of the user.
     * @return The ID of the user.
     */

    @Override
    public Long getUserId(Long chatId) {
        User user = userRepository.findByChatId(chatId).orElseThrow();
        return user.getId();
    }

    /**
     * Creates a new user with the given chat ID and username.
     * @param chatId The chat ID for the new user.
     * @param userName The username for the new user.
     */
    @Override
    public void createNewUser(Long chatId, String userName) {
        User user = new User();
        user.setChatId(chatId);
        user.setUserName(userName);

        Optional<Role> userRoleOpt = roleService.findByRoleName(RoleName.ROLE_USER);
        if (userRoleOpt.isPresent()) {
            Role userRole = userRoleOpt.get();
            List<Role> roles = new ArrayList<>();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            log.error("Attempted to add a non-existent role for user: " + userName + " with chat ID: " + chatId);

        }

        user.setCardState(UserCardInputState.NONE);
        adminService.adminNotify(userName, chatId);
        saveUser(user);
    }

    @Override
    public void addRole(Long userId, RoleName role) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Role> roleOpt = roleService.findByRoleName(role);

        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            Role userRole = roleOpt.get();
            List<Role> roles = user.getRoles();

            if (roles.contains(userRole)) {
                log.error("Attempted to add an already existent role " + role + " for user: " + userId);
            } else {
                roles.add(userRole);
                userRepository.save(user);
                messagingTemplate.convertAndSend("/topic/roles/" + userId, "Roles updated");
            }
        } else {
            log.error("No user or role");
        }
    }

    @Override
    public List<String> getCurrentUserRoles(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> value.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public void deleteRoleByUser(Long userId, RoleName roleName) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Role> roleOpt = roleService.findByRoleName(roleName);

        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            Role userRole = roleOpt.get();
            List<Role> roles = user.getRoles();

            if (roles.contains(userRole)) {
                roles.remove(userRole);
                messagingTemplate.convertAndSend("/topic/roles/" + userId, "Roles updated");
                userRepository.save(user);
            } else {
                log.error("Attempted to delete a not existent role " + roleName + " for user: " + userId);
            }
        } else {
            log.error("No user or role");
        }
    }


    /**
     * Checks if a user is authorized by comparing authentication ID and user ID.
     * @param authId The authentication ID.
     * @param userId The user ID.
     * @return True if the IDs match, false otherwise.
     */
    public boolean isAuthorized(Long authId, Long userId) {
        return authId.equals(userId);
    }
}
